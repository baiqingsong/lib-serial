package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class SerialPortFinder {

    private static final String TAG = "SerialPortFinder";

    public static class Driver {
        private final String mDriverName;
        private final String mDeviceRoot;
        private List<File> mDevices = null;

        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        public List<File> getDevices() {
            if (mDevices == null) {
                mDevices = new ArrayList<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getAbsolutePath().startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + file);
                            mDevices.add(file);
                        }
                    }
                }
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }

    private List<Driver> mDrivers = null;

    List<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new ArrayList<>();
            try (LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"))) {
                String l;
                while ((l = r.readLine()) != null) {
                    // driver name may contain spaces, do not extract with split()
                    String drivername = l.substring(0, 0x15).trim();
                    String[] w = l.split(" +");
                    if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                        Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                        mDrivers.add(new Driver(drivername, w[w.length - 4]));
                    }
                }
            }
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        List<String> devices = new ArrayList<>();
        try {
            for (Driver driver : getDrivers()) {
                for (File file : driver.getDevices()) {
                    devices.add(String.format("%s (%s)", file.getName(), driver.getName()));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error listing serial devices: " + e.getMessage());
        }
        return devices.toArray(new String[0]);
    }

    public String[] getAllDevicesPath() {
        List<String> devices = new ArrayList<>();
        try {
            for (Driver driver : getDrivers()) {
                for (File file : driver.getDevices()) {
                    devices.add(file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error listing serial device paths: " + e.getMessage());
        }
        return devices.toArray(new String[0]);
    }
}
