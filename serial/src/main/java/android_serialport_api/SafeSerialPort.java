package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * SerialPort 的安全包装类
 * <p>
 * 提供线程安全的 close 操作，防止重复关闭。
 */
public class SafeSerialPort extends SerialPort {

    private static final String TAG = "SafeSerialPort";
    private volatile boolean mClosed = false;
    private final Object mCloseLock = new Object();

    public SafeSerialPort(File device, int baudrate, int dataBits, int stopBits, char parity)
            throws SecurityException, IOException {
        super(device, baudrate, dataBits, stopBits, parity);
    }

    @Override
    public void close() {
        synchronized (mCloseLock) {
            if (mClosed) {
                Log.w(TAG, "Serial port already closed");
                return;
            }
            mClosed = true;
        }
        super.close();
    }

    /**
     * 串口是否已关闭
     */
    public boolean isClosed() {
        return mClosed;
    }
}
