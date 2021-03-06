package casia.isiteam.militaryqa.utils;

import casia.isiteam.militaryqa.common.AliasMapper;
import casia.isiteam.militaryqa.mapper.master.ResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EntityAliasExtractor {

    @Autowired
    ResultMapper resultMapper;

    public static Pattern pattern;
    public static Matcher matcher;

    /**
     * 实体别名提取方法
     *  1. 先获取实体名在处理后的不同格式
     *  2. 对不同格式的实体名利用正则进行别名抽取
     * @param entityName 实体名
     * @return 实体别名列表
     */
    public static Set<String> getEntityAlias(String entityName) {

        // 实体名的不同格式
        Set<String> originWords = new HashSet<>();
        // 实体最终的所有别名
        Set<String> aliases = new HashSet<>();

        // 无论如何，先添加一个跟问句预处理相同处理的别名
        aliases.add(MultiQaUtil.preProcessQuestion(entityName));

        /* 一、添加预处理后的原生词条不同格式 - origin_words*/
        originWords.add(entityName);
        // 将汉字处理为对应数字
        originWords.add(ChineseNumberUtil.convertString(entityName));
        // 去掉 -
        originWords.add(entityName.replace("-", "").replace("－", ""));
        originWords.add(ChineseNumberUtil.convertString(entityName.replace("-", "").replace("－", "")));
        // 去掉 空格
        originWords.add(entityName.replace(" ", ""));
        originWords.add(ChineseNumberUtil.convertString(entityName.replace(" ", "")));
        // 去掉除了中文、数字和英文的所有符号
        originWords.add(ChineseNumberUtil.convertString(entityName.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5]", "")));

        /* 二、添加原生词条处理后的不同词条部分 - part_words */
        // 1、原生词条直接添加
        Set<String> partWords = new HashSet<>(originWords);

        // 2、提取原生词条中的不同内容
        // 2.1 提取 括号 和 引号 内的内容
        String[] patterns = {
                "(.*)[(（](.*)[)）](.*)",
                "(.*)[“”\"](.*)[“”\"](.*)"
        };

        for (String word : originWords) {
            for (String p : patterns) {
                pattern = Pattern.compile(p);
                matcher = pattern.matcher(word);
                if (matcher.find()) {
                    aliases.add(MultiQaUtil.preProcessQuestion(matcher.group(2))); // 引号或括号内的直接添加到最终别名
                    partWords.add(matcher.group(1));
                    partWords.add(matcher.group(2));
                    partWords.add(matcher.group(3));
                    partWords.add(matcher.group(1) + matcher.group(3));
                }
            }
        }

        // 2.2 过滤结尾停用词 - part_words （结尾停用词还未标注完成，目前不需要过滤结尾停用词效果也可以）
//        for (String word : part_words) {
//            for (String entityStopWord : entityStopWords) {
//                if (word.endsWith(entityStopWord))
//                    word = word.replace(entityStopWord, "");
//            }
//        }

        // 2.3 以 不同符号 划分，得到不同片段 - part_words
        Set<String> set = new HashSet<>();
        for (String word : partWords) {
            set.addAll(Arrays.asList(word.split("/")));
            set.addAll(Arrays.asList(word.split(" ")));
            set.addAll(Arrays.asList(word.split("·")));
        }
        partWords.addAll(set);

        /* 三、正则提取，保留别名字段 */
        patterns = new String[]{
                "[a-zA-Z\\d\\s.·]+-?[a-zA-Z\\d.·]+(型|式|级|号|系列|)",
                "^\\w+-?[a-zA-Z\\d.·]+(型|式|级|号|系列|)",
                "^\\w+-?[a-zA-Z\\d.·]+\\w*(型|式|级|号|系列)",
                "^[a-zA-Z.·]+(型|式|级|号|系列|)",
                "[a-zA-Z.·]+-?[a-zA-Z0-9.·]+",
                "^[\\u4e00-\\u9fa5]+",
                "^[\\u4e00-\\u9fa5a-zA-Z\\d\\s.·]+-?[a-zA-Z\\d.·]+",
                "^[\\u4e00-\\u9fa5a-zA-Z]+-?[\\d]+",
                "[a-zA-Z]+[0-9]*"
        };

        for (String word : partWords) {
            for (String p : patterns) {
                pattern = Pattern.compile(p);
                matcher = pattern.matcher(word);
                if (matcher.find()) {
                    aliases.add(MultiQaUtil.preProcessQuestion(matcher.group()));
                }
            }
        }
        return aliases.stream().filter(alias -> (!AliasMapper.conceptsStopWords.contains(alias) && !checkAlias(alias))).collect(Collectors.toSet());
    }

    /** 最后一轮校验 alias */
    private static boolean checkAlias(String alias) {
        return "MM".equals(alias) || alias.length() < 2 || alias.matches("^[\\d.-]+$") || alias.matches("^[IV型号级]+$");
    }

    public static void main(String[] args) {
        System.out.println(EntityAliasExtractor.getEntityAlias("鹰击82-潜射反舰导弹(YJ-82/C-802)"));
        System.out.println(EntityAliasExtractor.getEntityAlias("麦克唐纳F-15E“打击鹰” 双座双发打击战斗机"));
        System.out.println(EntityAliasExtractor.getEntityAlias("麦道DC-8“SuperSixty” 4发涡轮风扇远程客机"));
        System.out.println(EntityAliasExtractor.getEntityAlias("韦伯利0.455英寸MarkIV转轮手枪"));
        System.out.println(EntityAliasExtractor.getEntityAlias("BAe146/Avro RJ4发涡扇短程支线飞机"));
        System.out.println(EntityAliasExtractor.getEntityAlias("呵呵YJ17·嘻嘻DK·哈哈DT97"));
    }
}
