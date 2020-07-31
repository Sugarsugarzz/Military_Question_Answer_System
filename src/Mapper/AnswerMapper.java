package Mapper;

import Model.Answer;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AnswerMapper {

    List<Answer> findByCountryAndCategory(@Param("country") String country, @Param("category") String category);

    List<Answer> findByEntity(@Param("entity") String entity);

    List<Answer> findByEntityAndAttrs(@Param("entity") String entity, @Param("attrs") List<String> attrs);

    List<Answer> findMaxByAttrInAllCategory(@Param("attr") String attr);

    List<Answer> findMinByAttrInAllCategory(@Param("attr") String attr);

    List<Answer> findMaxByAttrInSingleCategory(@Param("category") String category, @Param("attr") String attr);

    List<Answer> findMinByAttrInSingleCategory(@Param("category") String category, @Param("attr") String attr);

    List<Answer> findInSingleRangeByUnit(@Param("category") String category, @Param("attr") String attr,
                                          @Param("operator") String operator, @Param("unit_item") String unit_item);

    List<Answer> findInSingleRangeByTime(@Param("category") String category, @Param("attr") String attr,
                                                @Param("operator") String operator, @Param("time_item") String time_item);

    List<Answer> findInMultiRangeByUnit(@Param("category") String category, @Param("attr") String attr, @Param("operator_1") String operator_1,
                                               @Param("item_1") String item_1, @Param("operator_2") String operator_2, @Param("item_2") String item_2);

    List<Answer> findInMultiRangeByTime(@Param("category") String category, @Param("attr") String attr, @Param("operator_1") String operator_1,
                                               @Param("item_1") String item_1, @Param("operator_2") String operator_2, @Param("item_2") String item_2);

    void saveQAInfo(@Param("uid") String uid, @Param("q_time") String q_time, @Param("question") String question, @Param("answer") String answer);
}
