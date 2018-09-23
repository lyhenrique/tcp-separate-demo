package com.eric.utils;

/**
 * Created by WD41129 on 2018/9/11.
 */
public class FormatUtils {


    public static byte[] intToBytesLowToLow(int i) {
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    public static byte[] intToBytesHighToLow(int value )
    {
        byte[] src = new byte[4];
        src[0] =  (byte) ((value>>24) & 0xFF);
        src[1] =  (byte) ((value>>16) & 0xFF);
        src[2] =  (byte) ((value>>8) & 0xFF);
        src[3] =  (byte) (value & 0xFF);
        return src;
    }
}
