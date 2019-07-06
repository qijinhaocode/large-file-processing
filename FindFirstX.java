package xin.twodog.PingCAP;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 思路：
 * 1. 把所有的词存到hashmap里面
 * 2. 记录出现频率和出现次数。
 * 3. 简化条件：英文，一行一个单词，所有英文单词小写，或者不存在类似 Word 与 word 相同的情况
 * 4. 假设有60万个单词每个单词平均16字节，最坏情况下大概要不超1G内存
 * 5. 核心就是对文本进行分类统计
 * 6. 100G的数据的单词，最多有多少个，能否用int表示? 一个字母的单词保存到txt需要35791394134行, long最大： 9223372036854775806L
 * 7. 优化：建立两个Map,一个存当前出现频率是一的单词，另一个是存当前频率是大于1的单词，最后遍历的时候只用遍历第一个Map
 */

class WordsInfo {

    long firstApperIndex;
    long frequency;

    public WordsInfo(long firstApperIndex, long frequency) {
        this.firstApperIndex = firstApperIndex;
        this.frequency = frequency;
    }
}

public class FindFirstX {
    public static void main(String[] args) throws Exception {
        Long countIndex = 0L;// 当前访问索引
        Map<String, WordsInfo> wordsMap = new HashMap<>(); //存单词的图
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream("G:/wordTest.txt");
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                //  System.out.println(line);
                KeepWordsToMap(wordsMap, line.trim(), countIndex);// 保存到map
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        // 输出结果
        System.out.println("result: " + FindFirstSingleX(wordsMap));
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
        countIndex++;
    }

    /**
     * method：遍历map，得到第一次出现未重复的解
     *
     * @param wordsMap
     * @return
     */
    public static String FindFirstSingleX(Map<String, WordsInfo> wordsMap) {
        String result = "";
        long min = Long.MAX_VALUE;
        for (String s : wordsMap.keySet()) {
            if (wordsMap.get(s).frequency == 1 && wordsMap.get(s).firstApperIndex < min) {
                //  System.out.println("结果： " + s);
                result = s;
                min = wordsMap.get(s).firstApperIndex;
            }
        }
        return result;
    }

}
