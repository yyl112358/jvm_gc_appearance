package cn.wkiki;

/**
 * 此类用来测试jvm在内存分配逻辑上对大对象直接分配到老年代上
 * 执行时 需要给定 jvm参数
 */
public class LargeObjectDirectToTenured {

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

    1、可以看到整个VM 未发生GC，Eden space 被占用了 34% 其中包括了 bytes1 所引用的1M内存的对象。
    2、tenured generation 被占用了60% 6M 为 bytes2 所引用的内存对象


    若我们在执行时取消对VM参数 -XX:PretenureSizeThreshold={}的设置。再次执行后gc日志如下方，第二个日志输出所示
    此时VM 在内存部分当时的执行逻辑如下
    1、未发生GC前，bytes1所引用的1M内存与其他vm对象所引用的对象均在eden space内，占eden space的32%
    2、代码向vm申请 一块6M大小的内存，vm发现 eden space内以无法满足此次申请，触发minor gc
    3、因为之前存活下来的对象大于to space的大小1M ，所以bytes1 所引用的1M内存被担保机制直接复制到了老年代
    enured generation   total 10240K, used 1024K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  10% used [0x00000000ff600000, 0x00000000ff700010, 0x00000000ff700200, 0x0000000100000000)
    4、eden space的剩余部分对象被转移到了 from space
    5、bytes2 所引用的6M内存 被分配在 eden space

    !!!!若不指定 -XX:PretenureSizeThreshold={} 参数时，此值为0.也就是vm不会对任何大小的对象尝试直接分配到老年代上。所有对象都要遵守分代理论存活久了才会被移到老年代 !!!!!
    */


    static int _1M = 1024*1024;

    public static void main(String[] args) {
        byte[] bytes1 = new byte[_1M];
        byte[] bytes2 = new byte[6*_1M]; // 此对象会直接进入老年代
    }
}

/*Heap
        def new generation   total 9216K, used 2841K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,  34% used [0x00000000fec00000, 0x00000000feec6708, 0x00000000ff400000)
        from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
        tenured generation   total 10240K, used 6144K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  60% used [0x00000000ff600000, 0x00000000ffc00010, 0x00000000ffc00200, 0x0000000100000000)
        Metaspace       used 3215K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 348K, capacity 388K, committed 512K, reserved 1048576K*/



//未指定 PretenureSizeThreshold 时GC日志的输出

/*{Heap before GC invocations=0 (full 0):
        def new generation   total 9216K, used 2677K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,  32% used [0x00000000fec00000, 0x00000000fee9d638, 0x00000000ff400000)
        from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
        tenured generation   total 10240K, used 0K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,   0% used [0x00000000ff600000, 0x00000000ff600000, 0x00000000ff600200, 0x0000000100000000)
        Metaspace       used 3113K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 336K, capacity 388K, committed 512K, reserved 1048576K
        [GC (Allocation Failure) [DefNew: 2677K->583K(9216K), 0.0016581 secs] 2677K->1607K(19456K), 0.0016884 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
        Heap after GC invocations=1 (full 0):
        def new generation   total 9216K, used 583K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,   0% used [0x00000000fec00000, 0x00000000fec00000, 0x00000000ff400000)
        from space 1024K,  56% used [0x00000000ff500000, 0x00000000ff591d48, 0x00000000ff600000)
        to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        tenured generation   total 10240K, used 1024K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  10% used [0x00000000ff600000, 0x00000000ff700010, 0x00000000ff700200, 0x0000000100000000)
        Metaspace       used 3113K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 336K, capacity 388K, committed 512K, reserved 1048576K
        }
        Heap
        def new generation   total 9216K, used 6973K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        eden space 8192K,  78% used [0x00000000fec00000, 0x00000000ff23d8b0, 0x00000000ff400000)
        from space 1024K,  56% used [0x00000000ff500000, 0x00000000ff591d48, 0x00000000ff600000)
        to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
        tenured generation   total 10240K, used 1024K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        the space 10240K,  10% used [0x00000000ff600000, 0x00000000ff700010, 0x00000000ff700200, 0x0000000100000000)
        Metaspace       used 3187K, capacity 4496K, committed 4864K, reserved 1056768K
class space    used 344K, capacity 388K, committed 512K, reserved 1048576K*/

