package com.williwoodstudios.mildren.mildredsface;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceSelectionActivity extends Activity {
    private static final String LOG_TAG = "DeviceSelectionActivity";
    private ListView mListViewDevices;
    private BluetoothService mBluetoothService;
    private ArrayList<Entry> mEntryList = new ArrayList<>();
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.v(LOG_TAG, "user clicked " + position + " " + id);
            Intent resultData = new Intent();
            setResult(0, resultData);
            finish();
        }
    };
    private MyListAdapter mAdapter = new MyListAdapter();
    private BluetoothService.ScanListener mScanListener = new BluetoothService.ScanListener() {
        @Override
        public void deviceSeen(final BluetoothDevice device, final int rssi) {
            runOnUiThread(new Runnable() {
                public void run() {
                    haveScanned(device, rssi);
                }
            });
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof BluetoothService.LocalBinder) {
                mBluetoothService = ((BluetoothService.LocalBinder) service).getService();
                mBluetoothService.addScanListener(mScanListener);
                mBluetoothService.startScan();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_selection);

        mEntryList.add(new Heading());

        mListViewDevices = (ListView) findViewById(R.id.listViewDevices);
        mListViewDevices.setAdapter(mAdapter);
        mListViewDevices.setOnItemClickListener(mOnItemClickListener);

        Intent service = new Intent(this, BluetoothService.class);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void haveScanned(BluetoothDevice device, int rssi) {
        for (Entry e : mEntryList) {
            if (e instanceof DeviceEntry && ((DeviceEntry) e).getAddress().equals(device.getAddress())) {
                ((DeviceEntry) e).setRSSI(rssi);
                return;
            }
        }
        mEntryList.add(new DeviceEntry(device, rssi));
        mAdapter.dataChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothService != null) {
            mBluetoothService.stopScan();
        }
        unbindService(mServiceConnection);
    }

    abstract class Entry {
        abstract public View getView(View convertView, ViewGroup parent);

        abstract public int getViewType();
    }

    class Heading extends Entry {
        @Override
        public View getView(View convertView, ViewGroup parent) {
            TextView toReturn = new TextView(DeviceSelectionActivity.this);
            toReturn.setText("Hello!");
            return toReturn;
        }

        @Override
        public int getViewType() {
            return 0;
        }
    }

    ;

    class DeviceEntry extends Entry {
        private final BluetoothDevice mDevice;
        private int mRSSI;
        private boolean available;
        private View mView;
        private TextView mViewRSSI;

        public DeviceEntry(BluetoothDevice device, int rssi) {
            mDevice = device;
            mRSSI = rssi;
        }

        @Override
        public int getViewType() {
            return 1;
        }

        @Override
        public View getView(View convertView, ViewGroup parent) {
            mView = getLayoutInflater().inflate(R.layout.listitem_device, null);
            ((TextView) mView.findViewById(R.id.textViewDeviceAddress)).setText(mDevice.getAddress());
            ((TextView) mView.findViewById(R.id.textViewDeviceName)).setText(mDevice.getName());
            (mViewRSSI = (TextView) mView.findViewById(R.id.textViewRSSI)).setText("" + mRSSI);
            return mView;
        }

        public String getAddress() {
            return mDevice.getAddress();
        }

        public void setRSSI(int rssi) {
            if (rssi != mRSSI) {
                mRSSI = rssi;
                mViewRSSI.setText("" + mRSSI);
            }
        }
    }

    class MyListAdapter implements ListAdapter {
        private ArrayList<DataSetObserver> mObservers = new ArrayList<>();

        public void dataChanged() {
            for (DataSetObserver dso : mObservers) {
                dso.onChanged();
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            // TODO - make this valid.
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            Log.v(LOG_TAG, "Registering data set observer");
            mObservers.add(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mObservers.remove(observer);
        }

        @Override
        public int getCount() {
            return mEntryList.size();
        }

        @Override
        public Object getItem(int position) {
            return mEntryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mEntryList.get(position).getView(convertView, parent);
        }

        @Override
        public int getItemViewType(int position) {
            return mEntryList.get(position).getViewType();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return mEntryList.isEmpty();
        }
    }
}
