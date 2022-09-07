package com.seuic.scanner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
//import android.os.Message;
import android.util.Log;

@SuppressWarnings("unused")
public class ScannerService extends Service implements DecodeInfoCallBack {
	static final String TAG = "ScannerApiTest";
	Scanner scanner;
	private boolean mScanRunning = false;
	
	private  void log(String  string){
		Log.i(TAG, string);
	}
	
//	public static  void MyService(Context context){
//	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		scanner = ScannerFactory.getScanner(this);
		scanner.open();
		scanner.setDecodeInfoCallBack(this);
		scanner.enable();
		mScanRunning = true;
		new Thread(runnable).start();
	}

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
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		mScanRunning = false;
		ScannerKey.close();
		scanner.setDecodeInfoCallBack(null);
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
}
