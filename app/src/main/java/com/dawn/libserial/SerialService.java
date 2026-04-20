package com.dawn.libserial;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dawn.serial.LSerialUtil;

public class SerialService extends Service {
    private static final String TAG = "SerialService";
    private LSerialUtil serialUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startPort();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.i(TAG, "send msg");
            sendAsciiMsg("{\"cmd\":\"press\"}");
        }, 3000);
    }

    /**
     * 启动串口
     */
    private void startPort() {
        serialUtil = new LSerialUtil(
                LSerialUtil.SerialNameType.TYPE_TTYS_WK, 0, 9600,
                8, 2, 'N',
                LSerialUtil.SerialType.TYPE_HEX,
                new LSerialUtil.OnSerialListener() {
                    @Override
                    public void onOpenError(String portPath, Exception e) {
                        Log.e(TAG, "串口打开异常: " + portPath + ", " + (e != null ? e.getMessage() : ""));
                    }

                    @Override
                    public void onReceiveError(Exception e) {
                        Log.e(TAG, "串口接收异常: " + (e != null ? e.getMessage() : ""));
                    }

                    @Override
                    public void onSendError(Exception e) {
                        Log.e(TAG, "串口发送异常: " + (e != null ? e.getMessage() : ""));
                    }

                    @Override
                    public void onDataReceived(String data) {
                        Log.i(TAG, "串口接收到的数据: " + data);
                    }
                });
        Log.i(TAG, "开启串口, 连接状态: " + serialUtil.isConnected());
    }

    /**
     * 发送 HEX 数据
     */
    private void sendMsg(String hexStr) {
        if (serialUtil != null) {
            serialUtil.sendHex(hexStr);
        }
    }

    /**
     * 发送 ASCII 数据
     */
    private void sendAsciiMsg(String asciiStr) {
        if (serialUtil != null) {
            serialUtil.sendAsciiLine(asciiStr);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serialUtil != null) {
            serialUtil.disconnect();
            serialUtil = null;
        }
    }
}
