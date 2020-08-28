package top.yangyuscript.knowledge.utils;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 17:10 2020/8/22
 */
public class RandomNumUtil {
    public static int getRandom4Num(){
        int registCode = (int) (Math.random() * 9000 + 1000);
        return registCode;
    }

    public static void main(String[] args) {
        System.out.println(getRandom4Num());
    }
}
