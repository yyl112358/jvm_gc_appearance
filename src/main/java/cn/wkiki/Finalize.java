package cn.wkiki;

import java.beans.Transient;

public class Finalize {

    static TestClass staticInstance = null;

    public static class TestClass{

        @Override
        protected void finalize() throws Throwable {
            System.out.println("i'm executing finalize method,i will rescue myself");
            Finalize.staticInstance = this;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestClass transientInstance = new TestClass();
        transientInstance = null;
        System.gc();
        Thread.sleep(5*1000);
        if(staticInstance !=null){
            System.out.println("instance use finalize rescue success");
            staticInstance = null;
            System.out.println("set staticInstace to null");
        }else{
            System.out.println("instance use finalize rescue fail");
        }
        System.gc();
        System.out.println("finalize just run only once per object instance");
        Thread.sleep(10*1000);
    }
}
