package xin.twodog.PingCAP;

import java.io.File;
import java.io.FileInputStream;

public class Utils {

    /**
     * 随机生成单词
     *
     * @param min 最小长度
     * @param max 最大长度
     * @return
     */

    public static String creatWord(int min, int max) {
        int count = (int) (Math.random() * (max - min + 1)) + min;
        String str = "";
        for (int i = 0; i < count; i++) {
            str += (char) ((int) (Math.random() * 26) + 'a');
        }
        return str;
    }

    public static void main(String[] args) throws Exception {
        String strPath = "G:/test.txt";
        String a = "gsgsfghsh";
        System.out.println(a.hashCode() % 38);
        System.out.println(JSHash(a) % 38);
        System.out.println(FNVHash1(a) % 38);
        System.out.println(DEKHash(a) % 38);
        System.out.println(APHash(a) % 38);
        FileIO.delFolder("G:/123");
        File dirFile = new File("G:/PingCAP");
        String[] fileList = dirFile.list();
        for (String s : fileList
        ) {
            System.out.println(s);
        }
    }

    /**
     * 返回文件内存大小
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Long getFileMem(String filePath) {
        File localFile = new File(filePath);
        return localFile.length();
    }


    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void delFile(String filePath) {
        File localFile = new File(filePath);
        localFile.delete();
    }


    /**
     * DEKHash算法
     *
     * @param str
     * @return
     */
    public static int DEKHash(String str) {
        int hash = str.length();
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
        }
        return (hash & 0x7FFFFFFF);
    }

    /**
     * APHash算法
     *
     * @param str
     * @return
     */
    public static int APHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash ^= ((i & 1) == 0) ? ((hash << 7) ^ str.charAt(i) ^ (hash >> 3)) :
                    (~((hash << 11) ^ str.charAt(i) ^ (hash >> 5)));
        }
        return hash;
    }

    /**
     * 改进的32位FNV算法1
     *
     * @param data 字符串
     * @param data
     * @return int值
     */
    public static int FNVHash1(String data) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < data.length(); i++)
            hash = (hash ^ data.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }

    /**
     * JS hash 算法
     *
     * @param str
     * @return
     */
    public static int JSHash(String str) {
        int hash = 1315423911;
        for (int i = 0; i < str.length(); i++) {
            hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
        }
        return (hash & 0x7FFFFFFF);
    }
}

