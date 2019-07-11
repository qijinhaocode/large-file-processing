package xin.twodog.PingCAP;

import java.io.*;
import java.util.HashMap;

public class FileIO {
    // 用于创建测试用例
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();    //获取开始时间
        float a = 0.15F; // 随机输入一个不重复数据
        String str = "";
        File f = new File("G:/wordTest710.txt");
        FileOutputStream fop = new FileOutputStream(f, false);
        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
        BufferedWriter bw = new BufferedWriter(writer, 1 * 1024 * 1024);
        for (long i = 0L; i < 800000L; i++) {
            if (a < Math.random()) {
                bw.append("TWODOG");
                bw.append("\r\n");
                a = 2.0F;
            }
            str = Utils.creatWord(1, 100);
            bw.append(str);
            bw.append("\r\n");
            bw.append(str);
            bw.append("\r\n");
            bw.append(str);
            bw.append("\r\n");
            bw.append(str);
            bw.append("\r\n");
            bw.append(str);
            bw.append("\r\n");
            bw.append(str);
            bw.append("\r\n");
        }
        bw.append("xiaoxinniubi");
        bw.append("\r\n");

        writer.flush();
        bw.flush();
        fop.flush();
        writer.close();
        bw.close();
        fop.close();

        System.out.println("完成");
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("创建测试用例程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间

    }

    /**
     * 方法：把字符串写入文件
     *
     * @param line
     * @param ch
     * @param Index :大文件里出现位置的索引
     */
    public static void WriteToFile(String line, char ch, Long Index, BufferedWriter bw) throws IOException {

        bw.append(line + ch + Index + "\r\n");
    }

    public static void WriteToFile(String line, BufferedWriter bw) throws IOException {

        bw.append(line + "\r\n");
    }

    /**
     * 方法： 把大文件切割成小文件
     *
     * @param num_file       分割后的小文件数量
     * @param sourceFilePath 被分割源文件路径
     * @param desFolderPath  存放分割后目标文件夹路径
     * @param fileName       小目标文件标准名称
     * @param smallFileMem   小文件内存限制
     * @throws IOException
     */
    public static void cutLargeFile(int num_file, String sourceFilePath, String desFolderPath, String fileName, long smallFileMem) throws IOException {
        long hashMapMem = 0L;// 定义读取文件时候存储的hashmap空间
        final long tempMapMemLimit = 1024L * 1024L * 1024L * 14L;
        HashMap<String, Long> tempHashMap = new HashMap<>(); //维护一个减少小文件写入的hash表
        long startTime = System.currentTimeMillis();    //获取开始时间
        FileInputStream inputStream = null;
        BufferedInputStream bis = null;
        BufferedReader reader = null;
        // int num_file = 26;
        File[] files = new File[num_file];
        FileOutputStream[] fops = new FileOutputStream[num_file];
        OutputStreamWriter[] writers = new OutputStreamWriter[num_file];
        BufferedWriter[] bws = new BufferedWriter[num_file];
        for (int i = 0; i < num_file; i++) {
            files[i] = new File(desFolderPath + "/" + fileName + i + ".txt");
            fops[i] = new FileOutputStream(files[i], true);
            writers[i] = new OutputStreamWriter(fops[i], "UTF-8");
            bws[i] = new BufferedWriter(writers[i], 1 * 1024 * 1024);
        }

        try {
            Long index = 0L; //统计字符串在源文件中的位置
            inputStream = new FileInputStream(sourceFilePath);
            bis = new BufferedInputStream(inputStream); //带缓冲数组的输入流
            reader = new BufferedReader(new InputStreamReader(bis, "utf-8"), 1 * 1024 * 1024);
            String line;

            while ((line = reader.readLine()) != null) {
                String trueLine = line.trim();

              /*  System.out.println("tempHashMap.get(trueLine)  " + tempHashMap.get(trueLine));
                System.out.println("hashMapMem < (long)(1024 * 1024 * 1024 * 14) " + (hashMapMem < tempMapMemLimit));
                System.out.println("真假： " + tempHashMap.get(trueLine) == null && hashMapMem < tempMapMemLimit);*/
                if (tempHashMap.get(trueLine) == null && hashMapMem < tempMapMemLimit) {
                    tempHashMap.put(trueLine, 1L);
                    hashMapMem += (8L + 4L + (long) trueLine.length()); // hashcode占4字节，频率占8字节，字符串占 trueLine.length() 字节
                } else if (tempHashMap.get(trueLine) != null && hashMapMem < tempMapMemLimit) {
                    tempHashMap.put(line.trim(), tempHashMap.get(trueLine) + 1L);
                    hashMapMem += (8L + 4L + (long) trueLine.length());
                }

                if (tempHashMap.get(trueLine) < 2 || (tempHashMap.get(trueLine) == null && hashMapMem > (long) (1024 * 1024 * 1024 * 14))) {
                    int type = trueLine.hashCode() % num_file > 0 ? trueLine.hashCode() % num_file : -trueLine.hashCode() % num_file;
                    //System.out.println("type: " + type);
                    // System.out.println("line.trim().hashCode: " + line.trim().hashCode());
                    FileIO.WriteToFile(trueLine, '分', index, bws[type]);
                    index++;
                }

            }
            for (int i = 0; i < num_file; i++) {
                fops[i].flush();
                writers[i].flush();
                bws[i].flush();
                fops[i].close();
                writers[i].close();
                bws[i].close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (reader != null) {
                reader.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
        for (File file : files) {
            FileInputStream inputStream_re = null;
            BufferedInputStream bis_re = null;
            BufferedReader reader_re = null;
            System.out.println(file.length());
            if (file.length() > smallFileMem) {
                int copies = (int) (Math.ceil((double) file.length()) / (double) smallFileMem); // 分成copies份
                //int copies = 2; // 分成copies份
                File[] files_re = new File[copies];
                FileOutputStream[] fops_re = new FileOutputStream[copies];
                OutputStreamWriter[] writers_re = new OutputStreamWriter[copies];
                BufferedWriter[] bws_re = new BufferedWriter[copies];
                for (int i = 0; i < copies; i++) {
                    int fileIndex = i + num_file;
                    files_re[i] = new File(desFolderPath + "/" + fileName + (fileIndex) + ".txt");
                    fops_re[i] = new FileOutputStream(files_re[i], true);
                    writers_re[i] = new OutputStreamWriter(fops_re[i], "UTF-8");
                    bws_re[i] = new BufferedWriter(writers_re[i], 1 * 1024 * 1024);
                }
                try {
                    inputStream_re = new FileInputStream(file.getAbsoluteFile());
                    bis_re = new BufferedInputStream(inputStream_re); //带缓冲数组的输入流
                    reader_re = new BufferedReader(new InputStreamReader(bis_re, "utf-8"), 1 * 1024 * 1024);
                    String line;
                    String[] trueStr;//文本中真实字符串

                    while ((line = reader_re.readLine()) != null) {
                        trueStr = line.trim().split("分");
                        int type = Utils.APHash(trueStr[0]) % copies > 0 ? Utils.APHash(trueStr[0]) % copies : -Utils.APHash(trueStr[0]) % copies;
                        FileIO.WriteToFile(line.trim(), bws_re[type]);

                    }
                    for (int i = 0; i < copies; i++) {
                        fops_re[i].flush();
                        writers_re[i].flush();
                        bws_re[i].flush();
                        fops_re[i].close();
                        writers_re[i].close();
                        bws_re[i].close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream_re != null) {
                        inputStream_re.close();
                    }

                    if (reader_re != null) {
                        reader_re.close();
                    }
                    if (bis_re != null) {
                        bis_re.close();
                    }
                }
                num_file = num_file + copies;
                file.delete();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("大文件分成小文件程序运行时间：" + (endTime - startTime) + "ms");

    }


    /**
     * 清空文件夹
     *
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}


