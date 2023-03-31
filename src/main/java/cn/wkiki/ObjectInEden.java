package cn.wkiki;

/**
 * 此类用来测试jvm在内存分配逻辑上对对象直接分配在 eden分区上
 * 执行时 需要给定 jvm参数
 */
public class ObjectInEden {

    /*
    执行时 jdk版本选择 1.8 使用如下参数可看到 gc 日志打印 如代码下方注释所示
    -XX:+UseSerialGC
    -XX:PretenureSizeThreshold=5242880
    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintHeapAtGC
    -Xms20M
    -Xmx20M
    -Xmn10M
    VM 在内存部分当时的执行逻辑如下

    1、可以看出发生GC 之前 （尝试为bytes4 变量分配 4M内存） 发生了 Allocation Failure 分配失败，因为Eden取已被占用了8M的95%
    剩余部分已不足为4M内存分配空间
    2、触发一次minor gc，但是eden分区中的三个2M大小的对象,共6M依然存活，但是Eden分区的to space 仅为1M 无法存放存活下来的6M对象
    3、触发担保机制，Eden分区 存活下来的对象因为无法全部都转移到 to space，需要老年代担保分配，所以三个大对象直接进入老年代，从minor gc
    后的内存结果也可看到这个过程
    tenured generation   total 10240K, used 6144K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
       the space 10240K,  60% used [0x00000000ff600000, 0x00000000ffc00030, 0x00000000ffc00200, 0x0000000100000000)
    4、此时Eden分区内再无存活对象，直接清空Eden 区
    5、将新申请的4M对象的内存在Eden 区分配，Eden space 的8M被占用了50%
    */
    static int _M = 1024*1024;

    public static void main(String[] args) throws InterruptedException {
        byte[] bytes1 = new byte[2*_M];
        byte[] bytes2 = new byte[2*_M];
        byte[] bytes3 = new byte[2*_M];
        byte[] bytes4 = new byte[4*_M];
    }
}

/*{Heap before GC invocations=0 (full 0):
        def new generation   total 9216K, used 7797K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,  95% used [0x00000000fec00000, 0x00000000ff39d638, 0x00000000ff400000)
        from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
        tenured generation   total 10240K, used 0K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,   0% used [0x00000000ff600000, 0x00000000ff600000, 0x00000000ff600200, 0x0000000100000000)
        Metaspace       used 3203K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 346K, capacity 388K, committed 512K, reserved 1048576K
        [GC (Allocation Failure) [DefNew: 7797K->592K(9216K), 0.0055455 secs] 7797K->6736K(19456K), 0.0055838 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
        Heap after GC invocations=1 (full 0):
        def new generation   total 9216K, used 592K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,   0% used [0x00000000fec00000, 0x00000000fec00000, 0x00000000ff400000)
        from space 1024K,  57% used [0x00000000ff500000, 0x00000000ff594358, 0x00000000ff600000)
        to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        tenured generation   total 10240K, used 6144K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  60% used [0x00000000ff600000, 0x00000000ffc00030, 0x00000000ffc00200, 0x0000000100000000)
        Metaspace       used 3203K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 346K, capacity 388K, committed 512K, reserved 1048576K
        }
        Heap
        def new generation   total 9216K, used 4854K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,  52% used [0x00000000fec00000, 0x00000000ff0297c0, 0x00000000ff400000)
        from space 1024K,  57% used [0x00000000ff500000, 0x00000000ff594358, 0x00000000ff600000)
        to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        tenured generation   total 10240K, used 6144K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  60% used [0x00000000ff600000, 0x00000000ffc00030, 0x00000000ffc00200, 0x0000000100000000)
        Metaspace       used 3216K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 348K, capacity 388K, committed 512K, reserved 1048576K*/
