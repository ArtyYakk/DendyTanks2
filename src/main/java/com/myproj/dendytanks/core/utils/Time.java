package com.myproj.dendytanks.core.utils;

public class Time {
    public static final long SECOND = 1000000000l; // Количство наносекунд в секунде
        public static long get(){
            return System.nanoTime();
        }
}
