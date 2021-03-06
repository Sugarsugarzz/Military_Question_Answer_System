package casia.isiteam.militaryqa.service;

import casia.isiteam.militaryqa.common.AliasMapper;
import casia.isiteam.militaryqa.common.Constant;
import casia.isiteam.militaryqa.common.QuestionTemplate;
import casia.isiteam.militaryqa.mapper.master.AnswerMapper;
import casia.isiteam.militaryqa.model.Answer;
import casia.isiteam.militaryqa.utils.ProcessUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AnswerSearcherService {

    @Autowired
    AnswerMapper answerMapper;

    // 问题类型 - 默认 百科
    int Q_type = 1;
    // 答案类型 - 默认 实体
    int A_type = 2;

    /**
     * 判断问句模式，从数据库检索答案
     */
    public String getAnswer(Map<String, List<String>> parser_dict) {

        // 初始化
        Q_type = 1;
        A_type = 2;

        // 存储从数据库获取的答案
        List<Answer> answers = new ArrayList<>();

        // 模式匹配
        if (QuestionTemplate.patterns.get("热点查询模式").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "热点查询模式");
            Q_type = 3;
        }

        else if (QuestionTemplate.patterns.get("期刊查询模式").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "期刊查询模式");
            Q_type = 4;
        }

        else if (QuestionTemplate.patterns.get("报告查询模式").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "报告查询模式");
            Q_type = 5;
        }

        else if (QuestionTemplate.patterns.get("直达模式-头条").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "直达模式-头条");
            Q_type = 6;
            A_type = 1;
        }

        else if (QuestionTemplate.patterns.get("直达模式-百科").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "直达模式-百科");
            Q_type = 6;
            A_type = 2;
        }

        else if (QuestionTemplate.patterns.get("直达模式-订阅").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "直达模式-订阅");
            Q_type = 6;
            A_type = 3;
        }

        else if (QuestionTemplate.patterns.get("直达模式-我的收藏").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "直达模式-我的收藏");
            Q_type = 6;
            A_type = 4;
        }

        else if (QuestionTemplate.patterns.get("直达模式-浏览历史").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "直达模式-浏览历史");
            Q_type = 6;
            A_type = 5;
        }

        else if (QuestionTemplate.patterns.get("大类别名").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "大类别名");
            Q_type = 7;
            for (String category : parser_dict.get(Constant.Nature_Big_Category)) {
                // 数据库检索答案
                String childCategories = answerMapper.findChildrenByBigCategory(AliasMapper.BigCategory.get(category));
                answers.addAll(answerMapper.findSmallCategoryByChildren(Arrays.asList(childCategories.split(","))));
            }
        }

        else if (QuestionTemplate.patterns.get("小类别名").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "小类别名");
            for (String category : parser_dict.get(Constant.Nature_Small_Category)) {
                // 数据库检索答案
                answers.addAll(answerMapper.findBySmallCategory(AliasMapper.SmallCategory.get(category)));
            }
        }

        else if (QuestionTemplate.patterns.get("国家及类别名").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "国家及类别名");
            String country = AliasMapper.Country.get(parser_dict.get(Constant.Nature_Country).get(0));
            String category = AliasMapper.SmallCategory.get(parser_dict.get(Constant.Nature_Small_Category).get(0));
            // 数据库检索答案
            answers = answerMapper.findByCountryAndCategory(country, category);
        }

        else if (QuestionTemplate.patterns.get("单实体").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "单实体");
            Set<String> entities = AliasMapper.Entity.get(parser_dict.get(Constant.Nature_Entity).get(0));
            for (String entity : entities) {
                // 数据库检索答案
                answers.addAll(answerMapper.findByEntity(entity));
            }
        }

        else if (QuestionTemplate.patterns.get("多实体").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "多实体");
            Q_type = 2;
            for (String entity: parser_dict.get(Constant.Nature_Entity)) {
                for (String enty : AliasMapper.Entity.get(entity)) {
                    // 数据库检索答案
                    answers.addAll(answerMapper.findByEntity(enty));
                }
            }
        }

        else if (QuestionTemplate.patterns.get("单实体单属性/多属性").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "单实体单属性/多属性");
            A_type = 1;
            Set<String> entities = AliasMapper.Entity.get(parser_dict.get(Constant.Nature_Entity).get(0));
            for (String entity : entities) {
                List<String> attrs = new ArrayList<>();
                for (String attr: parser_dict.get(Constant.Nature_Attribute)) {
                    attrs.add(AliasMapper.Attribute.get(attr));
                }
                // 数据库检索答案
                answers.addAll(answerMapper.findByEntityAndAttrs(entity, attrs));
            }
        }

        else if (QuestionTemplate.patterns.get("多实体单属性/多属性").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "多实体单属性/多属性");
            A_type = 1;
            for (String entity: parser_dict.get(Constant.Nature_Entity)) {
                List<String> attrs = new ArrayList<>();
                for (String attr: parser_dict.get(Constant.Nature_Attribute)) {
                    attrs.add(AliasMapper.Attribute.get(attr));
                }
                for (String enty : AliasMapper.Entity.get(entity)) {
                    // 数据库检索答案
                    answers.addAll(answerMapper.findByEntityAndAttrs(enty, attrs));
                }
            }
        }

        else if (QuestionTemplate.patterns.get("单属性单类别单区间").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "单属性单类别单区间");
            String category = AliasMapper.SmallCategory.get(parser_dict.get(Constant.Nature_Small_Category).get(0));
            String operator = AliasMapper.Compare.get(parser_dict.get(Constant.Nature_Compare).get(0));
            String attr = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(0));
            List<String> time_items = ProcessUtil.processTime(parser_dict.get(Constant.Nature_Time));
            List<String> unit_items = ProcessUtil.processUnit(parser_dict.get(Constant.Nature_Unit));

            // 数据库检索答案
            if (time_items.isEmpty()) {
                answers = answerMapper.findInSingleRangeByUnit(category, attr, operator, unit_items.get(0));
            } else {
                answers = answerMapper.findInSingleRangeByTime(category, attr, operator, time_items.get(0));
            }
        }

        else if (QuestionTemplate.patterns.get("单属性单类别多区间").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "单属性单类别多区间");
            String category = AliasMapper.SmallCategory.get(parser_dict.get(Constant.Nature_Small_Category).get(0));
            String operator_1 = AliasMapper.Compare.get(parser_dict.get(Constant.Nature_Compare).get(0));
            String operator_2 = AliasMapper.Compare.get(parser_dict.get(Constant.Nature_Compare).get(1));
            String attr = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(0));
            List<String> time_items = ProcessUtil.processTime(parser_dict.get(Constant.Nature_Time));
            List<String> unit_items = ProcessUtil.processUnit(parser_dict.get(Constant.Nature_Unit));

            // 数据库检索答案
            if (time_items.isEmpty()) {
                answers = answerMapper.findInMultiRangeByUnit(category, attr, operator_1, unit_items.get(0),
                        operator_2, unit_items.get(1));
            } else {
                answers = answerMapper.findInMultiRangeByTime(category, attr, operator_1, time_items.get(0),
                        operator_2, time_items.get(1));
            }
        }

        else if (QuestionTemplate.patterns.get("多属性单类别单区间").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "多属性单类别单区间");
            String category = AliasMapper.SmallCategory.get(parser_dict.get(Constant.Nature_Small_Category).get(0));
            String operator_1 = AliasMapper.Compare.get(parser_dict.get(Constant.Nature_Compare).get(0));
            String operator_2 = AliasMapper.Compare.get(parser_dict.get(Constant.Nature_Compare).get(1));
            String attr_1 = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(0));
            String attr_2 = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(1));
            List<String> time_items = ProcessUtil.processTime(parser_dict.get(Constant.Nature_Time));
            List<String> unit_items = ProcessUtil.processUnit(parser_dict.get(Constant.Nature_Unit));

            // 数据库检索答案
            if (time_items.isEmpty()) {
                answers = answerMapper.findMultiAttrInSingleRangeByUnit(category, attr_1, operator_1, unit_items.get(0),
                        attr_2, operator_2, unit_items.get(1));
            } else {
                // 区分一下时间属性和数值属性的顺序
                if (parser_dict.get("pattern").indexOf(Constant.Nature_Unit) > parser_dict.get("pattern").indexOf(Constant.Nature_Time)) {
                    answers = answerMapper.findMultiAttrInSingleRangeByTimeAndUnit(category, attr_2, operator_2, unit_items.get(0),
                            attr_1, operator_1, time_items.get(0));
                } else {
                    answers = answerMapper.findMultiAttrInSingleRangeByTimeAndUnit(category, attr_1, operator_1, unit_items.get(0),
                            attr_2, operator_2, time_items.get(0));
                }
            }
        }

        else if (QuestionTemplate.patterns.get("全类别属性最值").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "全类别属性最值");
            String type = AliasMapper.Most.get(parser_dict.get(Constant.Nature_Most).get(0));
            String attr = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(0));

            // 数据库检索答案
            if ("max".equals(type)) {
                answers.addAll(answerMapper.findMaxByAttrInAllCategory(attr));
            } else if ("min".equals(type)) {
                answers.addAll(answerMapper.findMinByAttrInAllCategory(attr));
            }
        }

        else if (QuestionTemplate.patterns.get("单类别属性最值").contains(parser_dict.get("pattern"))) {
            log.info("与 {} 问句模式匹配成功！", "单类别属性最值");
            String category = AliasMapper.SmallCategory.get(parser_dict.get(Constant.Nature_Small_Category).get(0));
            String type = AliasMapper.Most.get(parser_dict.get(Constant.Nature_Most).get(0));
            String attr = AliasMapper.Attribute.get(parser_dict.get(Constant.Nature_Attribute).get(0));

            // 数据库检索答案
            if ("max".equals(type)) {
                answers.addAll(answerMapper.findMaxByAttrInSingleCategory(category, attr));
            } else if ("min".equals(type)) {
                answers.addAll(answerMapper.findMinByAttrInSingleCategory(category, attr));
            }
        }

        else {
            log.info("未找到相应问句模板！");
        }

        // 组装答案JSON
        JSONObject obj = assembleJson(parser_dict, answers);

        return obj.toJSONString();
    }

    /**
     * 组装JSON
     * @param answers 答案实体列表
     * @return JSON
     */
    public JSONObject assembleJson(Map<String, List<String>> parserDict, List<Answer> answers) {

        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        switch (Q_type) {

            // 百科模式
            case 1:
                obj.put("Q_type", Q_type);
                obj.put("A_type", A_type);
                for (Answer answer: answers) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("entity_id", answer.getEntity_id());
                    jsonObject.put("entity_name", answer.getEntity_name());
                    jsonObject.put("attr_name", answer.getAttr_name());
                    jsonObject.put("attr_value", answer.getAttr_value());
                    jsonArray.add(jsonObject);
                }
                obj.put("Answer", jsonArray);
                break;

            // 对比模式
            case 2:
                obj.put("Q_type", Q_type);
                for (Answer answer: answers) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("entity_id", answer.getEntity_id());
                    jsonObject.put("entity_name", answer.getEntity_name());
                    jsonArray.add(jsonObject);
                }
                obj.put("Answer", jsonArray);
                break;

            // 热点查询模式
            case 3:

            // 期刊查询模式
            case 4:

            // 报告查询模式
            case 5:
                obj.put("Q_type", Q_type);
                obj.put("Q_content", parserDict.get(Constant.Nature_Keywords));

                if (parserDict.get(Constant.Nature_Time).size() == 0) {
                    obj.put("Q_start_time", null);
                    obj.put("Q_end_time", null);
                } else if (parserDict.get(Constant.Nature_Time).size() == 1) {
                    String startTime = parserDict.get(Constant.Nature_Time).get(0);
                    String endTime = null;
                    String word = parserDict.get(Constant.Nature_Unit).get(0);
                    // 长度为1的在处理下，补充一个结束时间
                    try {
                        if (word.contains("年")) {
                            endTime = DateUtil.formatDateTime(DateUtil.offset(DateUtil.parse(startTime), DateField.YEAR, 1));
                        } else if (word.contains("月")) {
                            endTime = DateUtil.formatDateTime(DateUtil.offset(DateUtil.parse(startTime), DateField.MONTH, 1));
                        } else if (word.contains("周")) {
                            endTime = DateUtil.formatDateTime(DateUtil.offset(DateUtil.parse(startTime), DateField.DAY_OF_MONTH, 7));
                        } else if (word.contains("日") || word.contains("天") || word.contains("号")) {
                            endTime = DateUtil.formatDateTime(DateUtil.offset(DateUtil.parse(startTime), DateField.DAY_OF_MONTH, 1));
                        }
                    } catch (Exception e) {
                        log.error("查询模式补充结束时间出现错误：", e);
                    }
                    obj.put("Q_start_time", startTime);
                    obj.put("Q_end_time", endTime);
                } else {
                    Collections.sort(parserDict.get(Constant.Nature_Time));
                    obj.put("Q_start_time", parserDict.get(Constant.Nature_Time).get(0));
                    obj.put("Q_end_time", parserDict.get(Constant.Nature_Time).get(parserDict.get(Constant.Nature_Time).size() - 1));
                }
                break;

            // 直达模式
            case 6:
                obj.put("Q_type", Q_type);
                obj.put("Answer", A_type);
                break;

            // 辅助模式
            case 7:
                obj.put("Q_type", Q_type);
                List<String> categories = new ArrayList<>();
                for (Answer answer : answers) {
                    categories.add(answer.getEntity_name());
                }
                obj.put("Answer", categories);
                break;

            default: break;
        }

        return obj;
    }
}
