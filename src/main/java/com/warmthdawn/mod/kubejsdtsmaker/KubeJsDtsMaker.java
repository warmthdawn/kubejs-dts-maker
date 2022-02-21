package com.warmthdawn.mod.kubejsdtsmaker;

public class KubeJsDtsMaker {
    public static void test() {
        TestClass testClass = new TestClass();


    }
    public static class Test1 {
        public int test;

    }
    public interface Test2 {

    }
    static class TestClass extends Test1 {
        public float test;

    }

    public static void func(Test1 t) {

    }

    public static void func(Test2 t) {
        String su = new String("super");
        String suN = "super";
        String helo = "helo";
        String superhelo = "superhelo";

        System.out.println(su + helo == superhelo);
        System.out.println(suN + helo == superhelo);
        System.out.println(su.intern() + helo == superhelo);
        System.out.println(suN.intern() + helo == superhelo);
        System.out.println(suN == su);
        System.out.println(suN.intern() == su);
    }
}
