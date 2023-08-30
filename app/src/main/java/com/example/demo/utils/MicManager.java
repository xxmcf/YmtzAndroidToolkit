package com.example.demo.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MicManager {

    private final String TAG="MicManager";

    private AudioRecord audioRecorder = null;

    private int messageCode = 0;

    private int sampleRateInHz = 8000;
    private int channelConfig_in = AudioFormat.CHANNEL_IN_MONO;
    private int channelConfig_out = AudioFormat.CHANNEL_OUT_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private int recordMinBufSize = 0;
    private byte[] recordBuf = null;
    private RecordRunnable recordRunnable = null;
    private Handler handler;

    public static final int MESSAGE_DEFAULT = -1;


    public void setHandler(Handler handler, int messageCode){
        this.handler=handler;
        this.messageCode = messageCode;
    }

    private void sendMessage(byte[] data,int size){
        if (handler != null) {
            Message message=new Message();
            message.obj = data;
            message.arg1 = size;
            message.what = messageCode;
            handler.sendMessage(message);
        }
    }

    public void startRecord() {
        if (audioRecorder == null) {
            initRecord();
        }
        if (recordRunnable != null) {
            recordRunnable.stop();
            recordRunnable = null;
        }
        recordRunnable = new RecordRunnable();
        new Thread(recordRunnable).start();
    }

    public void destroy() {
        if (recordRunnable != null) {
            recordRunnable.stop();
            recordRunnable = null;
        }
        if (audioRecorder != null) {
            try {
                audioRecorder.stop();
                audioRecorder.release();
            } catch (IllegalStateException e) {
            }
        }
    }

    public void stopRecord() {
        if (recordRunnable != null) {
            recordRunnable.stop();
            recordRunnable = null;
        }
        messageCode = MESSAGE_DEFAULT;
    }

    private void initRecord() {
        recordMinBufSize = 2560; //AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig_in, audioFormat);
        Log.i(TAG,"recordMinBufSize="+recordMinBufSize);
        if (recordMinBufSize == AudioRecord.ERROR || AudioRecord.ERROR_BAD_VALUE == recordMinBufSize) {
            Log.e(TAG,"AudioRecord.getMinBufferSize failed!");
            return;
        }
        audioRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig_in, audioFormat, recordMinBufSize);
        if(recordBuf==null){
            recordBuf=new byte[recordMinBufSize];
        }
    }

    private class RecordRunnable implements Runnable {
        private boolean running = true;

        public void stop() {
            running = false;
        }

        private String TAG = "MicManager";
        
        @Override
        public void run() {
            if (audioRecorder == null) {
                Log.i(TAG,"audioRecorder is null");
                return;
            }
            int readResult = 0;

            audioRecorder.startRecording();

            while (running) {
                readResult = audioRecorder.read(recordBuf, 0, recordMinBufSize);
                if (readResult < 0) {
                    sleep(20);
                    continue;
                }
//                Log.i(TAG, "readBuf, length=" + recordBuf.length);

                sendMessage(recordBuf, readResult);

//                sleep(20);
            }
            if (audioRecorder != null) {
                try {
                    audioRecorder.stop();
                    audioRecorder.release();
                    audioRecorder = null;
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Record thread exception: " + e.getMessage());
                }
            }
        }

        private void sleep(int msecs) {
            try {
                Thread.sleep(msecs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
