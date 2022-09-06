package com.seuic.scanner_example;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.seuic.scanner.DecodeInfo;
import com.seuic.scanner.DecodeInfoCallBack;
import com.seuic.scanner.Scanner;
import com.seuic.scanner.ScannerFactory;
import com.seuic.scanner.ScannerKey;
import com.seuic.scanner.VideoCallBack;
import com.seuic.scanner.R;
import com.seuic.scanner_example.MainActivity;

@SuppressWarnings("unused")
public class ScannerService extends Service implements DecodeInfoCallBack,VideoCallBack {
	static final String TAG = "ScannerApiTest";
	Scanner scanner;
	private static  MainActivity mcontext = null;
	private boolean mScanRunning = false;
	
	private  void log(String  string){
		Log.i(TAG, string);
	}
	
	public static  void MyService(Context context){
		mcontext = (MainActivity)context;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		scanner = ScannerFactory.getScanner(this);
		scanner.open();
		scanner.setDecodeInfoCallBack(this);
		scanner.setVideoCallBack(this);
		scanner.enable();
		mScanRunning = true;
		new Thread(runnable).start();
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			int ret1 = ScannerKey.open();
			if (ret1 > -1) {
				while (mScanRunning) {
					int ret = ScannerKey.getKeyEvent();
					if (ret > -1) {
						switch (ret) {
						case ScannerKey.KEY_DOWN:
							if (scanner != null && mScanRunning) {
								scanner.startScan();
							}
							break;
						case ScannerKey.KEY_UP:
							if (scanner != null && mScanRunning) {
								scanner.stopScan();
							}
							break;
						}
					}
				}
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		mScanRunning = false;
		ScannerKey.close();
		scanner.setDecodeInfoCallBack(null);
		scanner.setVideoCallBack(null);
		scanner.close();
		scanner = null;
		super.onDestroy();
	}

	public static final String BAR_CODE = "barcode";
	public static final String CODE_TYPE = "codetype";
	public static final String LENGTH = "length";
	
	// this is a custom broadcast receiver action
	public static final String ACTION = "seuic.android.scanner.scannertestreciever";

	@Override
	public void onDecodeComplete(DecodeInfo info) {
		Intent intent = new Intent(ACTION);
		Bundle bundle = new Bundle();
		bundle.putString(BAR_CODE, info.barcode);
		bundle.putString(CODE_TYPE, info.codetype);
		bundle.putInt(LENGTH, info.length);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	@Override
	public boolean onVideoCallBack(int width, int height, byte[] img) {
		if (img == null||width == 0||height == 0||img.length == 0){
			return false;
		}
		
		log("onVideCallBack E");
		Message video_msg = mcontext.mHandler.obtainMessage(img.length, width, height, img);
		mcontext.mHandler.sendMessage(video_msg);
		
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log("onVideCallBack X");
		
		return false;
	}
	
}
