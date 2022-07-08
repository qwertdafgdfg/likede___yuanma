package com.lkd.service.impl.test;

import java.io.*;

public class ExportCode {

    static FileWriter fw;
    static BufferedWriter writer;
    static String headTitle = "＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝";
    static String endline="\n";
    public ExportCode() {}
    public ExportCode(String outputPath)
    {
        String os=System.getProperties().getProperty("os.name");
        if(os.startsWith("win")||os.startsWith("Win"))endline="\r\n";
        try {
            // 设置成尾部追加方式
            fw = new FileWriter(outputPath, true);
            writer = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param path  绝对路径
     * @param filename 要读的文件名
     */
    public void WriteToMyFile(String path, String filename) {
        if (!filename.endsWith(".java") )
            return;
        try {
            writer.write(endline+headTitle+endline);
            writer.write("『"+filename+"』");
            writer.write(endline+headTitle+endline);
            BufferedReader br = new BufferedReader(new FileReader(path));
            String buf = br.readLine();
            while (buf != null) {
                writer.write(buf + endline);
                buf = br.readLine();
            }
            // 输出到文件
            writer.flush();
            if (br != null)br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //递归遍历当前文件夹下的所有文件
    public void showAllSubFile(String path) {
        File f = new File(path);
        String[] list = f.list();

        for (String s : list) {
            // System.out.println(s);
            File subf = new File(f.getPath() + File.separator + s);
            // 如果当前s所代表的是文件夹
            if (subf.isDirectory())
                showAllSubFile(subf.getPath());
            else {
                WriteToMyFile(subf.getPath(), s);
            }
        }
    }

//    public static void main(String[] args) {
//
//        System.out.println("Export start....");
//        /*输入文件夹路径*/
//        String inpath="E:\\work\\course-likede-java\\lkd_parent";
//        /*输出文件的路径*/
//        String outpathString="E:\\work\\course-likede-java\\output.java";
//        new ExportCode(outpathString).showAllSubFile(inpath);
//        System.out.println("Export Complete.");
//        // 最后关掉输出流
//        try {
//            if (writer != null)
//                writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
