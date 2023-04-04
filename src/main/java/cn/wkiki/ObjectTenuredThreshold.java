package cn.wkiki;

public class ObjectTenuredThreshold {

    /* 需要使用的 jvm 参数
    -XX:MaxTenuringThreshold=3
    -Xms20m -Xmx20m -Xmn10m
    -XX:+PrintHeapAtGC -XX:+PrintGCDetails -XX:+PrintTenuringDistribution
    -XX:SurvivorRatio=8
     * 新生代 使用默认比例 8:1 且为了防止 可以得到 eden 区大小为 5m*0.8 = 4m 两个 survivor space 大小为512k
     * 思路为
     *
     */

    static int _M = 1024*1024,_K = 1024;

    public static void main(String[] args) {
        byte[] smallBytes = new byte[256*_K];
        for (int i = 0; i < 12; i++) {
            byte[] middleBytes = new byte[3*_M];
            middleBytes = null;
        }
    }
}
