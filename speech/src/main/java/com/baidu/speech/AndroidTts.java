package com.baidu.speech;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import android.util.Log;
import com.baidu.speech.tts.ITTSListener;

import java.util.HashMap;

/**
 * android原生API
 */
public class AndroidTts {
    private final String TAG = "AndroidTts";

    private AndroidTts() {
    }

    private static AndroidTts instance;

    private Context mContext;

    private boolean inited = false;

    private Handler mainHandler;

    private ITTSListener mITTSListener;

    private UtteranceProgressListener mListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            sendMessage(utteranceId, "播放开始", false, ActionCode.WHAT_TTS_START_CODE);
            mITTSListener.onPlayStart(utteranceId);
        }

        @Override
        public void onDone(String utteranceId) {
            sendMessage(utteranceId, "播放结束", false, ActionCode.WHAT_TTS_FINISH_CODE);
            mITTSListener.onPlayDone(utteranceId);
        }

        @Override
        public void onError(String utteranceId) {
            Log.i(TAG, "onError " + utteranceId);
        }
    };

    private TextToSpeech mSpeech;

    public static synchronized AndroidTts getInstance() {
        if (instance == null) {
            instance = new AndroidTts();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void initTts(ITTSListener listener) {
        mITTSListener = listener;
        mSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mSpeech.setSpeechRate(1.0f);
                    mSpeech.setPitch(1.0f);
                    mSpeech.setOnUtteranceProgressListener(mListener);
                    inited = true;
                }
            }
        });
    }

    public void release() {
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
            mSpeech = null;
            mContext = null;
        }
    }

    public boolean speak(String text) {
        return speak(text, "0");
    }

    public void stop() {
        mSpeech.stop();
    }

    public boolean speak(String text, String utteranceId) {
        if (inited) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            int result = mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            if (result == TextToSpeech.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    private void sendMessage(String utteranceId, String message, boolean isError, int action) {
        if (!isError) {
            if (mainHandler != null) {
                Message msg = Message.obtain();
                msg.what = action;
                msg.obj = utteranceId;
                mainHandler.sendMessage(msg);
            }
        }
    }
}
