package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    private static final String TAG = "SerialPort";

    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /***
     * 构造方法
     * @param device 串口文件
     * @param baudrate 波特率
     * @param dataBits 数据位
     * @param stopBits 停止位
     * @param parity   校验位
     */
    public SerialPort(File device, int baudrate, int dataBits, int stopBits, char parity)
            throws SecurityException, IOException {

        if (device == null) {
            throw new IllegalArgumentException("device must not be null");
        }
        if (!device.exists()) {
            throw new IOException("Serial port device not found: " + device.getAbsolutePath());
        }

        mFd = open(device.getAbsolutePath(), baudrate, dataBits, stopBits, parity);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException("Failed to open serial port: " + device.getAbsolutePath());
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public void close() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileInputStream = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close input stream: " + e.getMessage());
        }
        try {
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
                mFileOutputStream = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close output stream: " + e.getMessage());
        }
        try {
            if (mFd != null) {
                nativeClose();
                mFd = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to close native fd: " + e.getMessage());
        }
    }

    // 调用JNI中 打开方法的声明
    private native static FileDescriptor open(String path, int baudrate,
                                              int dataBits, int stopBits, char parity);

    private native void nativeClose();

    static {
        System.loadLibrary("serial_port");
    }
}