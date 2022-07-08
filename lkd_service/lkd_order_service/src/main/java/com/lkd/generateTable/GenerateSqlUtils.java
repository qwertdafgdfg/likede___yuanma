package com.lkd.generateTable;

import com.google.common.collect.Lists;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class GenerateSqlUtils{


    public static void generate(String name, Map<String, Object> root, String outFile) {
        FileWriter out = null;
        try {

            Configuration cfg = new Configuration(Configuration.getVersion());
            // 设定去哪里读取相应的ftl模板文件
            cfg.setClassForTemplateLoading(GenerateSqlUtils.class,"/ftl");
            // 在模板文件目录中找到名称为name的文件
            Template temp = cfg.getTemplate(name);

            // 通过一个文件输出流，就可以写到相应的文件中
            out = new FileWriter(new File(outFile));

            temp.process(root, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取minDate maxDate 之间月份
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getMonthBetween(LocalDate minDate, LocalDate maxDate){
        List<String> result = Lists.newArrayList();
        while (true){
            if(minDate.isAfter(maxDate)) break;
            result.add(minDate.format(DateTimeFormatter.ofPattern("yyyy_MM")));

            minDate = minDate.plusMonths(1);
        }

        return result;
    }
}
