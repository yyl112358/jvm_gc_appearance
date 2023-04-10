package cn.wkiki;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomRefTest {

    /*
     使用jvm 参数 观察GC
    -Xms30m
    -Xmx30m
    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintHeapAtGC
     */

    static int _M = 1024 * 1024, _K = 1024;

    public static void main(String[] args) {

        ReferenceQueue<byte[]> referenceQueue = new ReferenceQueue<>();

        byte[] bytes = new byte[2 * _M];
        PhantomReference<byte[]> phantomReference = new PhantomReference(bytes, referenceQueue);
        bytes = null;
        System.gc();
        try {
            Thread.sleep(200);
            Reference<byte[]> reference = (Reference<byte[]>) referenceQueue.remove(1000);
            if (reference != null) {
                if (reference == phantomReference) {
                    System.out.println("phantomReference 引用的对象已不存在");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
