package Searcher;

import Utils.FileOperator;
import com.hankcs.hanlp.dictionary.CustomDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictMapper {

    /**
     * 构建（问句中涉及的词形式，数据库中标准词形式）的映射关系
     * 用于在数据库中检索答案
     */

    // 国家
    public static Map<String, String> Country = buildCountry();
    // 一级分类
    public static Map<String, String> BigCategory = buildBigCategory();
    // 二级分类
    public static Map<String, String> SmallCategory = buildSmallCategory();
    // 实体
    public static Map<String, String> Entity = buildEntity();
    // 实体属性
    public static Map<String, String> Attribute = buildAttribute();
    // 比较词
    public static Map<String, String> Compare = buildCompare();
    // 最值
    public static Map<String, String> Most = buildMost();


    private static Map<String, String> buildCountry() {
        String filepath = "data/dict_for_match_query/country.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildBigCategory() {
        String filepath = "data/dict_for_match_query/big_category.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildSmallCategory() {
        String filepath = "data/dict_for_match_query/small_category.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildEntity() {
        String filepath = "data/dict_for_match_query/entity.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildAttribute() {
        String filepath = "data/dict_for_match_query/attribute.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildCompare() {
        String filepath = "data/dict_for_match_query/compare.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    private static Map<String, String> buildMost() {
        String filepath = "data/dict_for_match_query/most.txt";
        return FileOperator.matchFileToMap(filepath);
    }

    /**
     * 将<时间>处理成标准形式
     * @param times 问句中的时间项
     * @return 标准形式列表
     */
    public static List<String> processTime(List<String> times) {
        List<String> time_items = new ArrayList<>();
        for (String item: times) {
            // 处理成 YYYY-mm-dd 的标准形式
            Matcher m_year = Pattern.compile("\\d{4}年").matcher(item);
            Matcher m_month = Pattern.compile("\\d{1,2}月").matcher(item);
            Matcher m_day = Pattern.compile("\\d{1,2}日").matcher(item);
            String year = "", month = "", day = "", date = "";
            if (m_year.find()) {
                year = m_year.group().replace("年", "");
                if (m_month.find())
                    month = m_month.group().replace("月", "");
                if (m_day.find())
                    day = m_day.group().replace("日", "");
                date = year + "-" + dateCompletion(month) + "-" + dateCompletion(day);
            }
            time_items.add(date);
        }
//        System.out.println(time_items);
        return time_items;
    }

    /**
     * 将<单位数值>处理成标准形式
     * @param units 问句中的单位数值项
     * @return 标准形式列表
     */
    public static List<String> processUnit(List<String> units) {
        List<String> unit_items = new ArrayList<>();
        for (String item: units) {
            Matcher m_unit = Pattern.compile("[0-9]+(.[0-9]+)?").matcher(item);
            while (m_unit.find())
                unit_items.add(m_unit.group());
        }
//        System.out.println(unit_items);
        return unit_items;
    }

    /**
     * 日期补全，一位的前面补0
     * @param date 原始日期
     * @return 处理后的日期
     */
    private static String dateCompletion(String date) {
        if (date.equals(""))
            date = "01";
        if (Integer.parseInt(date) < 10 && date.length() < 2)
            date = "0" + date;
        return date;
    }

}
