package casia.isiteam.militaryqa.service;

import casia.isiteam.militaryqa.common.AliasMapper;
import casia.isiteam.militaryqa.common.Constant;
import casia.isiteam.militaryqa.mapper.master.AnswerMapper;
import casia.isiteam.militaryqa.mapper.master.ResultMapper;
import casia.isiteam.militaryqa.model.DictMatcher;
import casia.isiteam.militaryqa.model.Result;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class InitService {

    @Autowired
    ResultMapper resultMapper;
    @Autowired
    AnswerMapper answerMapper;


    /**
     * 程序首次启动，初始化 CustomDictionary 和 AliasMapper
     * ==================================================================================
     *   CustonDictionary未生效可以先删除 data/dictionary/custom/CustomDictionary.txt.bin
     * ==================================================================================
     */
    public void initCustomDictionaryAndAliasMapper() {
        log.info("【InitCustomDictionaryAndAliasMapper】正在获取 match_dict 表，并初始化 HanLP(CustomDictionary) 和 AliasMapper...");
        try {
            List<DictMatcher> matchers = answerMapper.getMatchDict();
            log.info("match_dict 加载成功，size：{}", matchers.size());

            for (DictMatcher matcher : matchers) {
                String[] aliases = matcher.getAlias().split("\\|");

                // init CustomDictionary
                for (String alias : aliases) {
                    CustomDictionary.add(alias, matcher.getLabel());
                }

                // init AliasMapper
                if (Constant.Nature_Entity.equals(matcher.getLabel())) {
                    Arrays.stream(aliases).forEach(alias -> {
                        if (!AliasMapper.Entity.containsKey(alias)) {
                            // 一个别名对应多个实体
                            AliasMapper.Entity.put(alias, new HashSet<>());
                        }
                        AliasMapper.Entity.get(alias).add(matcher.getWord());
                    });
                } else {
                    switch (matcher.getLabel()) {
                        case Constant.Nature_Country:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.Country.put(alias, matcher.getWord())); break;
                        case Constant.Nature_Big_Category:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.BigCategory.put(alias, matcher.getWord())); break;
                        case Constant.Nature_Small_Category:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.SmallCategory.put(alias, matcher.getWord())); break;
                        case Constant.Nature_Attribute:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.Attribute.put(alias, matcher.getWord())); break;
                        case Constant.Nature_Compare:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.Compare.put(alias, matcher.getWord())); break;
                        case Constant.Nature_Most:
                            Arrays.stream(aliases).forEach(alias -> AliasMapper.Most.put(alias, matcher.getWord())); break;
                        default:  break;
                    }
                }
            }
            log.info("【InitCustomDictionaryAndAliasMapper】初始化成功.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【InitCustomDictionaryAndAliasMapper】初始化失败！", e);
        }
    }

    /**
     * 初始化 ConceptsStopWords，存入 most, country, compare, attribute, big_category, small_category
     */
    public void initConceptsStopWords() {
        try {
            log.info("【initConceptsStopWords】初始化中...");
            List<String> items = resultMapper.getConceptsStopWords();
            items.forEach(item -> {
                AliasMapper.conceptsStopWords.addAll(Arrays.asList(item.split("\\|")));
            });
            log.info("【initConceptsStopWords】初始化成功.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【initConceptsStopWords】初始化失败！");
        }
    }

    /**
     * 初始化 smallCategoriesMapping，用于新数据分类
     */
    public void initSmallCategories() {
        try {
            log.info("【initSmallCategories】初始化中...");
            List<Result> records = resultMapper.getSmallCategories();
            records.forEach(r -> {
                if (!"其他".equals(r.getAlias())) {
                    String[] aliases = r.getAlias().split("\\|");
                    for (String alias : aliases) {
                        Constant.smallCategoriesMapping.put(alias, r.getC_id());
                    }
                }
            });
            log.info("【initSmallCategories】初始化成功.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【initSmallCategories】初始化失败！");
        }
    }

    /**
     * 清空DictMapper
     */
    private void clearDictMapper() {
        AliasMapper.Country.clear();
        AliasMapper.BigCategory.clear();
        AliasMapper.SmallCategory.clear();
        AliasMapper.Entity.clear();
        AliasMapper.Attribute.clear();
        AliasMapper.Compare.clear();
        AliasMapper.Most.clear();
    }
}
