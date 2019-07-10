package xin.twodog.PingCAP;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * 定义字符串信息
 */
class WordsInfo {
    String word;
    long firstApperIndex;
    long frequency;

    public WordsInfo(long firstApperIndex, long frequency) {
        this.firstApperIndex = firstApperIndex;
        this.frequency = frequency;
    }

    public WordsInfo(String word, long firstApperIndex, long frequency) {
        this.word = word;
        this.firstApperIndex = firstApperIndex;
        this.frequency = frequency;
    }
}


public class FindFirstX {
    /**
     * 主函数入口
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        int num_files = 5;// 被分割文件数量
        // String sourceFilePath = "G:/wordTest710.txt"; // 100G大文件路径
        String sourceFilePath = "D:/面试/pingCAP/test.txt"; // 100G大文件路径
        FileIO.delAllFile("G:/PingCAP");
        String desFolderPath = "G:/PingCAP"; //切割后的小文件存放路径
        String fileName = "wordShow"; // 小目标文件标准名称
        String[] strTemp; // 存放字符串与出现位置的数组
        String result = "全文无非重复字符串"; // 保存最终结果
        WordsInfo wordsInfo; //存放每个小文件中最有可能的目标解信息
        Long firstApperIndex = Long.MAX_VALUE;
        FileInputStream inputStream = null;
        BufferedInputStream bis = null;
        BufferedReader reader = null;
        FileIO.cutLargeFile(num_files, sourceFilePath, desFolderPath, fileName); //按照内存限制切割小文件
        for (int i = 0; i < num_files; i++) {
            Map<String, WordsInfo> wordsMap = new HashMap<>(); //存单词的容器
            try {
                inputStream = new FileInputStream(desFolderPath + "/" + fileName + i + ".txt");
                bis = new BufferedInputStream(inputStream); //带缓冲数组的输入流
                reader = new BufferedReader(new InputStreamReader(bis, "utf-8"), 1 * 1024 * 1024);
                String line;
                while ((line = reader.readLine()) != null) {

                    strTemp = line.trim().split("分");
                    KeepWordsToMap(wordsMap, strTemp[0], Long.valueOf(strTemp[1])); // 保存到容器
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
            wordsInfo = FindFirstSingleX(wordsMap);
            if (wordsInfo.frequency == 1 && wordsInfo.firstApperIndex < firstApperIndex) {
                firstApperIndex = wordsInfo.firstApperIndex;
                result = wordsInfo.word;
            }
        }

        System.out.println("第一个不重复的字符串为： " + result); // 输出结果
        long endTime = System.currentTimeMillis();
        System.out.println("程序总运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间
    }

    /**
     * method ：把每个字符串存进当前map，并记录其，第一次出现的位置以及出现频率
     *
     * @param wordsMap
     * @param s
     * @param countIndex
     */
    public static void KeepWordsToMap(Map<String, WordsInfo> wordsMap, String s, Long countIndex) {

        if (wordsMap.get(s) != null)
            wordsMap.replace(s.trim(), new WordsInfo(wordsMap.get(s.trim()).firstApperIndex, wordsMap.get(s.trim()).frequency + 1L));
        else wordsMap.put(s, new WordsInfo(countIndex, 1L));

    }

    /**
     * method：遍历map，得到第一次出现未重复的解,若无返回默认解
     *
     * @param wordsMap
     * @return
     */
    public static WordsInfo FindFirstSingleX(Map<String, WordsInfo> wordsMap) {
        String result = "";
        long minFirstApperIndex = Long.MAX_VALUE;
        long frequency = 2;
        WordsInfo wordsInfo = new WordsInfo(result, minFirstApperIndex, frequency);
        for (String s : wordsMap.keySet()) {
            if (wordsMap.get(s).frequency == 1 && wordsMap.get(s).firstApperIndex < minFirstApperIndex) {
                wordsInfo.word = s;
                wordsInfo.firstApperIndex = wordsMap.get(s).firstApperIndex;
                minFirstApperIndex = wordsMap.get(s).firstApperIndex;
                wordsInfo.frequency = 1;
            }
        }

        return wordsInfo;
    }

}
