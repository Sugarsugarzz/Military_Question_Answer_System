package Test;

import QA.MilitaryQA;
import QA.MultiMilitaryQA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestQA {

    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        MultiMilitaryQA QA = new MultiMilitaryQA();
        
        while (true) {
        	String question = new Scanner(System.in).next();
        	QA.qa_main("小黑", question, sf.format(new Date()));
        }

        // 多轮测试
//        QA.qa_main("小黑", "我想找中国的神舟七号", sf.format(new Date()));
//        QA.qa_main("小黑", "歼-10战斗机的长度", sf.format(new Date()));
//        QA.qa_main("小黑", "神舟六号载人飞船的长度", sf.format(new Date()));  // 神舟六号没有长度属性lol
//        QA.qa_main("小黑", "他们的生产商", sf.format(new Date()));

        // 国家及类别名
//        QA.qa_main("小明", "中国的战斗机有哪些？", sf.format(new Date()));

        // 单类别名
//        QA.qa_main("小明", "战斗机有哪些？", sf.format(new Date()));

        // 单实体
//        QA.qa_main("我想找中国的神舟七号");

        // 多实体
//        QA.qa_main("我想找中国的神舟五号和神舟七号");

        // 单实体单属性/多属性
//        QA.qa_main("神舟七号的长度、发射地点、生产商、原产国、简介？");

        // 多实体单属性/多属性
//        QA.qa_main("神舟五号、神舟七号和歼-20战斗机的长度、发射地点、生产商、原产国和简介？");

        // 全类别属性最值
//        QA.qa_main("武器装备里长度最长的是哪个？");

        // 单类别属性最值
//        QA.qa_main("战斗机里长度最短的是哪个？");

        // 单属性单类别单区间
//        QA.qa_main("小明", "长度大于25米的战斗机有哪些？", sf.format(new Date()));
//        QA.qa_main("发射日期大于2011年的宇宙飞船？");
//        QA.qa_main("宇宙飞船里发射日期在2011年之后的有哪些？");

        // 单属性单类别多区间
//        QA.qa_main("小明", "发射日期小于2010年且大于2005年的宇宙飞船", sf.format(new Date()));

        // 多属性单类别多区间
//        QA.qa_main("小明", "长度大于25米且高度大于5米的战斗机有哪些？？", sf.format(new Date()));
//        QA.qa_main("小黑", "首飞时间晚于2010年且高度大于4米的战斗机有哪些？？", sf.format(new Date()));

    }
}
