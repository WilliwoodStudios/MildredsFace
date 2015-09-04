package com.williwoodstudios.mildren.mildredsface;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by robwilliams on 15-09-02.
 */
public class BluetoothService extends Service {
    public interface ScanListener {
        void deviceSeen(BluetoothDevice device, int rssi);
    };

    private ArrayList<ScanListener> mListeners = new ArrayList<>();

    public void addScanListener(ScanListener listener) {
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    public void removeScanListener(ScanListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    private static final String LOG_TAG = "BluetoothService";


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private LocalBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startScan() {
        UUID[] mServiceUIDs = new UUID[]{RX_SERVICE_UUID};
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startLeScan(mServiceUIDs, mLeScanCallback);
    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(LOG_TAG, "Scanned: " + device.getName() + " " + device.getAddress() + " " + device.getBluetoothClass() + " " + rssi);
            ScanListener[] dummy = new ScanListener[0];
            synchronized (mListeners) {
                dummy = mListeners.toArray(dummy);
            }

            for (ScanListener listener : dummy) {
                listener.deviceSeen(device, rssi);
            }
        }
    };
}
