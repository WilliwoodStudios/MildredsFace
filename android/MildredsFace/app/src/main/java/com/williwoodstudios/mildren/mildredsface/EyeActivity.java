package com.williwoodstudios.mildren.mildredsface;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EyeActivity extends ActionBarActivity {

    private int crossMode = 0;

    private EyeView.PositionChangedListener mPositionChangedListener = new EyeView.PositionChangedListener() {
        @Override
        public void positionChanged(EyeView eyeView, double pupilX, double pupilY) {
            if (crossMode == 1) {
                mViewLeftEye.setPosition(pupilX,pupilY);
                mViewRightEye.setPosition(pupilX,pupilY);
            } else if (crossMode == 2) {
                if (eyeView == mViewLeftEye) {
                    mViewRightEye.setPosition(-pupilX,pupilY);
                } else {
                    mViewLeftEye.setPosition(-pupilX,pupilY);
                }
            } else if (crossMode == 3) {
                if (eyeView == mViewLeftEye) {
                    mViewRightEye.setPosition(-pupilX,-pupilY);
                } else {
                    mViewLeftEye.setPosition(-pupilX,-pupilY);
                }
            }
        }
    };
    private EyeView mViewLeftEye;
    private EyeView mViewRightEye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye);
        mViewLeftEye = (EyeView)findViewById(R.id.viewLeftEye);
        mViewRightEye =(EyeView)findViewById(R.id.viewRightEye);

        mViewLeftEye.setPositionChangedListener(mPositionChangedListener);
        mViewRightEye.setPositionChangedListener(mPositionChangedListener);
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
            crossMode+=1;
            crossMode %= 4;
            
            String message = null;
            switch(crossMode) {
                case 0:
                    message = "Normal";
                    break;
                case 1:
                    message = "Follow";
                    break;
                case 2:
                    message = "Crossed";
                    break;
                case 3:
                    message = "Inverted";
                    break;
            }

            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
