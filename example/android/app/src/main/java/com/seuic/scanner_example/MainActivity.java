package com.seuic.scanner_example;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
//import android.os.Bundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.seuic.scanner.Scanner;
import com.seuic.scanner.ScannerFactory;

public class MainActivity extends FlutterActivity {
    static final String TAG = "ScannerApiTest";
    private static final String CHANNEL = "com.seuic.scanner/plugin";
    //static final int SCANNER_KEYCODE = 142;
    EditText mEditText;
    Button btn_setvalue;
    Button btn_getvalue;
    EditText edt_id;
    EditText edt_value;
    ScanReceiver receiver;
    IntentFilter filter;
    MainOnClickListener clickListener = new MainOnClickListener();
    Scanner scanner;

    public Handler mHandler =  new VideoHandler() ;
    public Handler mImageHandler = new ImageHandler();
    private ImageView image = null;
    boolean startViewImage = false;
    boolean videoFinished = true;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                    (call, result) -> {
        this.setContentView(R.layout.main);

        // Call the service when the program is opened, you can call only once, do not need to call in each activity
        Intent intent = new Intent(this, ScannerService.class);
        startService(intent);

        //Each activitys to register the receiver to receive scan service passed over the bar code
        // use custom broadcast receiver (you can define action yourself)
        // Registering and unloading sinks is recommended on onResume and onPause
        receiver = new ScanReceiver();
        filter = new IntentFilter(ScannerService.ACTION);

        init();
                }
        );
    }

    void init() {
        scanner = ScannerFactory.getScanner(this);
        if (scanner == null){
            log("scanner(NULL)");
        }

        mEditText = (EditText) findViewById(R.id.text);
        btn_getvalue = (Button) findViewById(R.id.btn_getvalue);
        btn_setvalue = (Button) findViewById(R.id.btn_setvalue);

        findViewById(R.id.bt_startVideo).setOnClickListener(clickListener);
        findViewById(R.id.bt_getLastImage).setOnClickListener(clickListener);
        btn_getvalue.setOnClickListener(clickListener);
        btn_setvalue.setOnClickListener(clickListener);
        edt_id = (EditText) findViewById(R.id.edt_id);
        edt_value = (EditText) findViewById(R.id.edt_value);
        image =  (ImageView) findViewById(R.id.image);
        image.setVisibility(View.GONE);
        image.setOnClickListener(clickListener);


    }


    private  void log(String  string){
        Log.i(TAG, string);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ScannerService.MyService(this);
        // Register the receiver
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        // Unregister the receiver
        unregisterReceiver(receiver);
        scanner.stopVideo();
        scanner.stopScan();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, ScannerService.class);
        this.stopService(intent);
        super.onDestroy();
    }

    class MainOnClickListener implements OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_getvalue:
                    getValueToShow();
                    break;
                case R.id.btn_setvalue:
                    setValue();
                    break;
                case R.id.bt_startVideo:
                    startVideo();
                    break;
                case R.id.bt_getLastImage:
                    getLastImage();
                    break;
                case R.id.image:
                    if (videoFinished) {
                        image.setVisibility(View.GONE);
                    } else {
                        stopVideo();
                    }
                    break;
            }
        }
    }

    void setValue() {
        try {
            int id = Integer.parseInt(edt_id.getText().toString());
            int value = Integer.parseInt(edt_value.getText().toString());
            // Set Params
            if (scanner.setParams(id, value)) {
                showMsg(getString(R.string.set_param_successfully));
            } else {
                showMsg(getString(R.string.errot_settings_failed));
            }
        } catch (Exception ex) {
            showMsg(getString(R.string.error) + ex.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    void getValueToShow() {
        try {
            int id = Integer.parseInt(edt_id.getText().toString());
            // Get Params
            int value = scanner.getParams(id);
            edt_value.setText(value + "");
        } catch (Exception ex) {
            showMsg(getString(R.string.error) + ex.getMessage());
        }
    }

    void showMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public class ScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            mEditText.append(getString(R.string.barcode)
                    + bundle.getString(ScannerService.BAR_CODE) + getString(R.string.type)
                    + bundle.getString(ScannerService.CODE_TYPE) + getString(R.string.length)
                    + bundle.getInt(ScannerService.LENGTH) + "\n");
        }
    }

    private void getLastImage() {
        if (Build.VERSION.SDK_INT > 19) {
            if (scanner != null) {
                byte[] picture;
                picture = scanner.getLastImage();
                if (picture != null) {
                    Message picture_msg = mImageHandler.obtainMessage(picture.length, picture);
                    mImageHandler.sendMessage(picture_msg);
                }
            }
        }
    }

    private void startVideo()
    {
        Thread mThread = new Thread(runnable);
        mThread.start();
    }

    private void stopVideo()
    {
        if (scanner != null){
            videoFinished = true;
            startViewImage = false;
            scanner.stopVideo();
            image.setVisibility(View.GONE);
        }
    }

    private final Runnable runnable  = new Runnable() {
        public void run() {
            if (scanner != null && videoFinished){
                log("start Video");
                videoFinished = false;
                startViewImage = false;
                scanner.startVideo(10000);
                videoFinished = true;
            }
        }
    };

    public class VideoHandler extends Handler {
        public void handleMessage(Message msg) {

            log("VideoHandler E");
            if (!videoFinished){
                Bitmap bmSnap = BitmapFactory.decodeByteArray((byte[]) msg.obj,0,msg.what);
                if (bmSnap == null){
                    log("bmSnap(NULL)");
                }
                log("bmSnap done");
                if (!startViewImage){
                    startViewImage = true;
                    image.setVisibility(View.VISIBLE);
                }

                image.setImageBitmap(bmSnap);
            }else{
                log("VideoHandler  videoFinished(true)");
            }
        }
    }

    public class ImageHandler extends Handler {
        public void handleMessage(Message msg) {
            Bitmap bmSnap = BitmapFactory.decodeByteArray((byte[]) msg.obj,0,msg.what);
            if (bmSnap == null){
                log("bmSnap(NULL)");
            }

            log("bmSnap done");
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(bmSnap);
        }
    }
}
