package cn.wkiki;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class WeakRefTest {

    static int _M = 1024*1024,_K = 1024;

    /*
    -Xms30m
    -Xmx30m
    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintHeapAtGC
     */

    public static void main(String[] args) {
        byte[][] bytesArr = new byte[10][];
        WeakReference<byte[]>[] weakReferences = new WeakReference[10];
        for (int i = 0; i < 10; i++) {
            byte[] bytes = new byte[5*_M];
            // bytesArr[i] = bytes;   // 注释掉此行没有强引用后，则不会发生oom
            WeakReference<byte[]> weakReference = new WeakReference<>(bytes);
            weakReferences[i] = weakReference;
        }
        testSoftRef();
    }

    private static void testSoftRef(){
        byte[][] bytesArr = new byte[10][];
        SoftReference<byte[]>[] softReferences = new SoftReference[10];
        for (int i = 0; i < 10; i++) {
            byte[] bytes = new byte[5*_M];
            // bytesArr[i] = bytes;   // 注释掉此行没有强引用后，则不会发生oom
            SoftReference<byte[]> softReference = new SoftReference<>(bytes);
            softReferences[i] = softReference;
        }
    }
}
