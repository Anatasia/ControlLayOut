package com.example.controllayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.softsec.service.ASService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final String TAG = "Record";
	private String appleDailyPackage = "com.nextmedia";
	private static ASService serviceInstance=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//苹果动新闻com.nextmedia
		Button appleDailyButton = (Button) findViewById(R.id.apple_daily);
		appleDailyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/apple_daily.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
		
		//台湾壹周刊
		Button twNextMediaMagazine = (Button) findViewById(R.id.tw_nextmedia_magazine);
		twNextMediaMagazine.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/tw_nextmedia_magazine.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
		
		//台湾苹果日报
		Button nextmediatwButton = (Button) findViewById(R.id.nextmediatw);
		nextmediatwButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/nextmediatw.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
		
		//一周plus
		Button oneplusButton = (Button) findViewById(R.id.oneplus);
		oneplusButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/oneplus.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
		
		//电子报
		
		Button epaperButton = (Button) findViewById(R.id.epaper);
		epaperButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/epaper.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
		
		
		//华闻头条
		Button overseajdButton = (Button) findViewById(R.id.overseajd);
		overseajdButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getServiceInstance()!=null){
					getServiceInstance().testApp(Environment.getExternalStorageDirectory().getPath().toString()+"/script/overseajd.txt");
				}else{
					Log.i(TAG, "service为空");
				}
			}
		});
	}

	
    
    private ASService getServiceInstance(){
        if (! isAccessibilitySettingsOn(getApplicationContext())) {
            Toast.makeText(MainActivity.this, "尚未开启辅助功能，请在新弹出的对话框中开启ASDemo辅助功能！", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return null;
        }else{
            serviceInstance=ASService.getInstance();
        }
        return serviceInstance;
    }
    
    private boolean isAccessibilitySettingsOn(Context mContext) {

        int accessibilityEnabled = 0;
        final String service = "com.example.controllayout/com.softsec.service.ASService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.i(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.i(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.i(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.i(TAG, "We've found the correct setting - accessibility is switched on!");
                        accessibilityFound=true;
                        return true;
                    }
                }
            }
        } else {
            Log.i(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
