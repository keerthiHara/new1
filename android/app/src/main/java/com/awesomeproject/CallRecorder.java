package com.awesomeproject;

import android.content.Context;
import android.media.MediaRecorder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;

public class CallRecorder extends ReactContextBaseJavaModule {
    private static final String TAG = "CallRecorder";
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private String outputFilePath;

    public CallRecorder(ReactApplicationContext reactContext) {
        super(reactContext);
        TelephonyManager telephonyManager = (TelephonyManager) reactContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        startRecording();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (isRecording) {
                            stopRecording();
                        }
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public String getName() {
        return "CallRecorder";
    }

    @ReactMethod
    public void startRecording(Promise promise) {
        if (isRecording) {
            promise.reject("Recording Error", "Recording is already in progress.");
            return;
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        outputFilePath = getReactApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/call_recording.mp4";
        recorder.setOutputFile(outputFilePath);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            promise.resolve("Recording started");
            Log.d(TAG, "Recording started");
        } catch (IOException e) {
            e.printStackTrace();
            promise.reject("Recording Error", e.getMessage());
            Log.e(TAG, "Recording failed: " + e.getMessage());
        }
    }

    @ReactMethod
    public void stopRecording(Promise promise) {
        if (!isRecording) {
            promise.reject("Recording Error", "No recording is in progress.");
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        promise.resolve("Recording stopped");
        Log.d(TAG, "Recording stopped");
    }
}
