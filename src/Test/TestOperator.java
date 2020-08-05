package Test;

import Utils.DbOperator;
import Utils.FileOperator;

import java.util.List;
import java.util.Map;

public class TestOperator {
    public static void main(String[] args) {

        /*
            将数据库中查出的列表项存入词典文件中。
         */
//        // 获取 Entity
//        List<String> entities = DbOperator.getEntities();
//        String filepath = "data/dict_for_match_query/entity.txt";
//        FileOperator.entitiesToFile(entities, filepath);
//
//        // 获取 Big Category
//        List<String> bigCategories = DbOperator.getBigCategory();
//        filepath = "data/dict_for_match_query/big_category.txt";
//        FileOperator.entitiesToFile(bigCategories, filepath);
//
//        // 获取 Small Category
//        List<String> smallCategories = DbOperator.getSmallCategory();
//        filepath = "data/dict_for_match_query/small_category.txt";
//        FileOperator.entitiesToFile(smallCategories, filepath);
//
//        // 获取 Attribute
//        List<String> attributes = DbOperator.getAttributes();
//        filepath = "data/dict_for_match_query/attribute.txt";
//        FileOperator.entitiesToFile(attributes, filepath);

        /*
            从文件中获取 （问句中涉及的词形式，数据库中标准形式）对
         */
//        String filepath = "data/dict_for_match_query/country.txt";
//        Map<String, String> res = FileOperator.matchFileToMap(filepath);
//        for (Map.Entry<String, String> entry: res.entrySet()) {
//            System.out.println("Key：" + entry.getKey() + ", Value：" + entry.getValue());
//        }

        /*
            将 data/dict_for_match_query 下所有涉及的实体词，都加入到分词器的分词词典中
         */
//        FileOperator.matchFileToSegFile();

        /*
            获取 Entity及其别名，以（实体：实体别名列表）形式写入match文件中，并将所有实体别名加入分词器词典
         */
        Map<String, String > map = DbOperator.getEntitiesAndSameas();
        String filepath = "data/dict_for_match_query/entity2.txt";
        String target_filepath = "data/dict_for_segment/entity2.txt";
        FileOperator.mapToMatchFile(map, filepath);
        FileOperator.matchFileToSegFile(filepath, target_filepath);




    }
}