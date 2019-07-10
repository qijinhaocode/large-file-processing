package xin.twodog.PingCAP;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 思路：
 * 1. 把所有的词存到hashmap里面
 * 2. 记录出现频率和出现次数。
 * 3. 简化条件：英文，一行一个单词，所有英文单词小写，或者不存在类似 Word 与 word 相同的情况
 * 4. 假设有60万个单词每个单词平均16字节，26.8M内存
 * 5. 核心就是对文本进行分类统计
 * 6. 100G的数据的单词，最多有多少个，能否用int表示? 一个字母的单词保存到txt需要35791394134行, long最大： 9223372036854775806L
 * 7. 优化：建立两个Map,一个存当前出现频率是一的单词，另一个是存当前频率是大于1的单词，最后遍历的时候只用遍历第一个Map
 * 8. 缓冲内存的最优值是多少?
 * 9.  随机字符串，把读出来的数据，存到另一个TXT文件中。
 * 10. 文件读入，若有两个以上重复的只保存一个，最坏的情况就是所有字符串都不重复
 * 11. 只能扫描一遍，避免每个词都之和其他词最对比输出
 * 12. 遇到重复的，要么销毁一个，要么全部销毁
 * 13. 分成7个文件，每个文件大概 ，计数需要 long型 8个字节，每个文件处理，按照出现频率排序，之后只保留第一个值。
 * 14. 每个文件放多少值可以满足一次map排序的需求，用“？”分割
 * 15. 经过计算，分成36个文件夹进行操做？保证哈希算法分的均匀就是   100/36=2.777
 * 所以保证哈希算法分成的每个文件不超过2.7G（事实上文件中还要包括一个一记录出现位置的字符，所以2.7G是上限）
 */


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
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();    //获取开始时间
        int num_files = 10;// 被分割文件数量
        String sourceFilePath = "G:/wordTest710.txt";
        String desFolderPath = "G:/PingCAP";
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
                String line = "";
                while ((line = reader.readLine()) != null) {

                    strTemp = line.trim().split("%");
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
        // 输出结果
        // System.out.println("result: " + FindFirstSingleX(wordsMap));
        System.out.println("第一个不重复的字符串为： " + result);
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序总运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
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
        // countIndex++;
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
                wordsInfo.frequency = 1;
            }
        }

        return wordsInfo;
    }

}
