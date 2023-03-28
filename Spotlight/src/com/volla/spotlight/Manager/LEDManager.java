package com.volla.spotlight.Manager;

import android.util.Log;

import com.volla.spotlight.Utils.FileUtils;

public class LEDManager {
    public static final String BASELEDPATH = "/sys/devices/platform/soc/11eb3000.i2c/i2c-9/9-0020/leds/aw210xx_led/";

    public int getNumLEDs() {
        return 10;
    }

    public void enableHW(boolean enable) {
        FileUtils.writeLine(BASELEDPATH + "hwen", enable ? "1" : "0");
    }

    public void setLEDBrightness(int ledno, float brightness) {
        if (ledno >= 9)
            ledno = 0;
        else
            ledno++;
        String light = ledno + " " + (int) (brightness * 99.0f) + " 99";
        FileUtils.writeLine(BASELEDPATH + "singleled", light);
    }

    public boolean supportsBlink() {
        return true;
    }

    public void setLEDsBlink(boolean enable) {
        FileUtils.writeLine(BASELEDPATH + "effect", enable ? "5" : "0");
    }

    public void enableAllLEDs(boolean enable) {
        FileUtils.writeLine(BASELEDPATH + "effect", enable ? "1" : "0");
    }
}
