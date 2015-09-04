package com.williwoodstudios.mildren.mildredsface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class EyeActivity extends Activity {

    private static final String LOG_TAG = "mildred";

    private int crossMode = 0;
    private EyeView mViewLeftEye;
    private EyeView mViewRightEye;
    private EyeView.PositionChangedListener mPositionChangedListener = new EyeView.PositionChangedListener() {
        @Override
        public void positionChanged(EyeView eyeView, double pupilX, double pupilY) {
            if (crossMode == 1) {
                mViewLeftEye.setPosition(pupilX, pupilY);
                mViewRightEye.setPosition(pupilX, pupilY);
            } else if (crossMode == 2) {
                if (eyeView == mViewLeftEye) {
                    mViewRightEye.setPosition(-pupilX, pupilY);
                } else {
                    mViewLeftEye.setPosition(-pupilX, pupilY);
                }
            } else if (crossMode == 3) {
                if (eyeView == mViewLeftEye) {
                    mViewRightEye.setPosition(-pupilX, -pupilY);
                } else {
                    mViewLeftEye.setPosition(-pupilX, -pupilY);
                }
            }
        }
    };
    private BluetoothService mBluetoothService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG, "Service connected.");
            if (service instanceof BluetoothService.LocalBinder) {
                mBluetoothService = ((BluetoothService.LocalBinder) service).getService();

                // TODO do something... ???
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Service disconnected.");
            mBluetoothService = null;
        }
    };
    private ImageButton mImageButtonEyeMode;
    private View.OnClickListener mImageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            crossMode+=1;
            crossMode %= 4;

            switch(crossMode) {
                case 0:
                    mImageButtonEyeMode.setImageResource(R.drawable.icons_eyes);
                    break;
                case 1:
                    mImageButtonEyeMode.setImageResource(R.drawable.icons_match);
                    break;
                case 2:
                    mImageButtonEyeMode.setImageResource(R.drawable.icons_mirror);
                    break;
                case 3:
                    mImageButtonEyeMode.setImageResource(R.drawable.icons_invert);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye);
        mViewLeftEye = (EyeView) findViewById(R.id.viewLeftEye);
        mViewRightEye = (EyeView) findViewById(R.id.viewRightEye);

        mViewLeftEye.setPositionChangedListener(mPositionChangedListener);
        mViewRightEye.setPositionChangedListener(mPositionChangedListener);

        mImageButtonEyeMode = (ImageButton) findViewById(R.id.imageButtonEyeMode);
        mImageButtonEyeMode.setImageResource(R.drawable.icons_eyes);
        mImageButtonEyeMode.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageButtonEyeMode.setOnClickListener(mImageButtonOnClickListener);

        Log.d(LOG_TAG, "About to try binding");

        Intent serviceIntent = new Intent();
        serviceIntent.setClass(this, BluetoothService.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, requestCode + " " + resultCode + " " + data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eye, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

//            Dialog dialog = new Dialog(this);
//            dialog.setContentView(R.layout.activity_device_selection);
//            dialog.show();

            Intent toOpen = new Intent(this, DeviceSelectionActivity.class);
            startActivityForResult(toOpen, 0);
        }

        return super.onOptionsItemSelected(item);
    }
}
