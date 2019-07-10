package xin.twodog.PingCAP;

import java.io.*;

public class FileIO {
    // 用于创建测试用例
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();    //获取开始时间
        float a = 0.5F; // 随机输入一个不重复数据
        String str = "";
        File f = new File("G:/wordTest710.txt");
        FileOutputStream fop = new FileOutputStream(f, true);
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

    /**
     * 方法： 把大文件切割成小文件
     *
     * @param num_file       分割后的小文件数量
     * @param sourceFilePath 被分割源文件路径
     * @param desFolderPath  存放分割后目标文件夹路径
     * @param fileName       小目标文件标准名称
     * @throws IOException
     */
    public static void cutLargeFile(int num_file, String sourceFilePath, String desFolderPath, String fileName) throws IOException {

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
            // reader = new BufferedReader(new InputStreamReader(bis, "utf-8"), 1 * 1024 * 1024);
            reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
            String line = "";

            while ((line = reader.readLine()) != null) {
                int type = line.trim().hashCode() % num_file > 0 ? line.trim().hashCode() % num_file : -line.trim().hashCode() % num_file;
                //System.out.println("type: " + type);
                // System.out.println("line.trim().hashCode: " + line.trim().hashCode());
                FileIO.WriteToFile(line.trim(), '分', index, bws[type]);
                index++;

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


