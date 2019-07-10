package xin.twodog.PingCAP;

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
}
