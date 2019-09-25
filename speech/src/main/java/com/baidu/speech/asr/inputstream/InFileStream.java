package com.baidu.speech.asr.inputstream;

import android.app.Activity;
import com.baidu.speech.asr.util.MyLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class InFileStream {

    private static Activity context;

    private static final String TAG = "InFileStream";

    public static void setContext(Activity context) {
        InFileStream.context = context;
    }

    private static String filename;

    private static InputStream is;

    public static void reset() {
        filename = null;
        is = null;
    }

    public static void setFileName(String filename) {
        InFileStream.filename = filename;
    }

    public static void setInputStream(InputStream is2) {
        is = is2;
    }

    /**
     * 默认从createFileStream中读取InputStream
     * @return
     */
    public static InputStream create16kStream() {
        if (is != null) { // 默认为null，setInputStream调用后走这个逻辑
            return new FileAudioInputStream(is);
        } else if (filename != null) { //  默认为null， setFileName调用后走这个逻辑
            try {
                return new FileAudioInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // 默认从createFileStream中读取
            return new FileAudioInputStream(createFileStream());
        }
        return null;
    }

    private static InputStream createFileStream() {
        try {
            InputStream is = context.getAssets().open("outfile.pcm");
            MyLogger.info(TAG, "create input stream ok " + is.available());
            return is;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}