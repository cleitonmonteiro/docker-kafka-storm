package io.github.cleitonmonteiro.helper;

public class TimerHelper {
    public static void waitForSeconds (int seconds) {
        try{
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {

        }
    }

    public static void waitForMillis (long milliseconds) {
        try{
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {

        }
    }
}
