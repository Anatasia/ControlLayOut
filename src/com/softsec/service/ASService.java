package com.softsec.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BufferedHeader;

import com.example.controllayout.MainActivity;
import com.softsec.thread.ScreenShotThread;

import android.R.integer;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ASService extends AccessibilityService {

	private String TAG = "Record";
	private static ASService serviceInstance;
	private String appleDailyPackage = "com.nextmedia";//苹果日报包名
	private final int CLICK = 1;//按钮点击
	private final int TEXT = 2;//文本信息比对
	private final int PICSHOT = 3;//截图
	
	public void onServiceConnected() {
		serviceInstance = this;
	}

	public boolean onUnbind(Intent intent) {
		serviceInstance = null;
		return true;
	}

	// 返回服务实例
	public static ASService getInstance() {
		return serviceInstance;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		Log.i(TAG, "当前运行的应用是:" + runningActivity);
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		serviceInstance = null;
	}

	//------------>苹果日报测试<-------------
	public void testAppleDaily() {
		
		if(openApp(appleDailyPackage)){
			Log.i(TAG, "打开苹果动新闻成功");
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				Log.i(TAG, e1.toString());
			}
			//String str1 = "000000";
			//String str2 = "000101";
			ArrayList<String> textInfos = new ArrayList<String>();
			textInfos.add("網絡連線緩慢，請耐心等候 3 101");
			textInfos.add("未能與伺服器連線，請再試 3 101");
			textInfos.add("線路非常繁忙，請稍後再試，謝謝！ 0");
			textInfos.add("网络连线缓慢，请耐心等候 3 101");
			textInfos.add("未能与伺服器连线，请再试 3 101");
			if(runTextScript(textInfos)){
				
				Intent intent = new Intent();
	            intent.setClass(ASService.this, MainActivity.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
			}else{
				Log.i(TAG,"无法进行判断");
			}
		}else{
			Log.i(TAG,"打开苹果日报应用失败");
		}
	}
	
	public void testApp(String fileName){
		//脚本处理
		ArrayList<String> textInfos = new ArrayList<String>();//文本信息
		ArrayList<String> idInfos = new ArrayList<String>();//id信息
		String packageNameString = "";//应用包名
		boolean isText = false;
		File file = new File(fileName);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			packageNameString = line;
			while((line = br.readLine())!=null){
				if(line.equals("text")){
					isText = true;
				}else if(line.equals("id")){
					isText = false;
				}else{
					if(isText){
						textInfos.add(line);
					}else{
						idInfos.add(line);
					}
				}
			}
			if(openApp(packageNameString)){
				Log.i(TAG, "打开应用成功");
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					Log.i(TAG, e1.toString());
				}
				
				if(textInfos.size()>0){
					Log.i(TAG, "进行文本内容比对测试");
					if(runTextScript(textInfos)){
						Intent intent = new Intent();
			            intent.setClass(ASService.this, MainActivity.class);
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(intent);
					}
				}
				
				if(idInfos.size()>0){
					Log.i(TAG,"进行控件ID测试");
					//if(runScript(str, textInfo, type))
				}
				
			}else{
				Log.i(TAG,"打开应用失败");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	//按文本内容进行控件查找脚本执行
	private boolean runTextScript(ArrayList<String> textInfos){
		int totalCount = 25;
		boolean isStop = false;
		int picCount = 1;
		for(int count = 0;count<totalCount&&!isStop;count++){
			Log.i(TAG, "第"+(count+1)+"/"+totalCount+"次页面获取");
			AccessibilityNodeInfo rootItem = getRootInActiveWindow();
			for(int i=0;i<textInfos.size();i++){
				String[] text = textInfos.get(i).split("   ");
				if(text.length==2||text.length==3){
					List<AccessibilityNodeInfo> nodeList = rootItem.findAccessibilityNodeInfosByText(text[0]);
					if(nodeList.size()>0){
						Log.i(TAG, nodeList.get(0).getText().toString());
						//截图
						ScreenShotThread screenShotThread = new ScreenShotThread("/AppTest/"+picCount+".png");
						ScreenShotThread.isScreenShotThreadOver=false;
						screenShotThread.start();
						while(true){
							if(ScreenShotThread.isScreenShotThreadOver){
								break;
							}
						}
						picCount++;
						if(text.length==2){
							if(text[1].equals("0")){
								Log.i(TAG, "该文本存在，结束");
								return true;
							}else if(text[1].equals("1")){
								Log.i(TAG, "该节点可直接进行点击，点击！");
								AccessibilityNodeInfo node = nodeList.get(0);
								node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
								node.getParent().getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
							}
						}else if(text.length==3){
							Log.i(TAG, "该文本存在，总共有："+nodeList.size()+"个");
							
					
							int parentCount = Integer.parseInt(text[1]);
							
							AccessibilityNodeInfo node = nodeList.get(0);
							for(int j=0;j<parentCount&&node!=null;j++){
								node = node.getParent();
							}
							for(int j=0;j<text[2].length();j++){
								int index = Integer.parseInt(text[2].substring(j,j+1));
								Log.i(TAG, "index:"+index);
								Log.i(TAG,"子节点个数："+node.getChildCount());
								if(node.getChildCount()>index){
									node = node.getChild(index);
								}else{
									Log.i(TAG, "没有子节点，停止查找");
									break;
								}
							}
							
							if(node!=null){
								Log.i(TAG, "找到按钮");
								node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							}else{
								Log.i(TAG, "未找到按钮");
							}
						}
						break;
					}else{
						Log.i(TAG,"未找到内容:"+text[0]);
					}
				}
			}
			
			try {
				Log.i(TAG, "休息10s，等待下一页面出现");
				Thread.sleep(10*1000);
				
			} catch (InterruptedException e) {
				Log.i(TAG,e.toString());
				// TODO: handle exception
			}
				
		}
		Log.i(TAG, "失败");
		
		return false;
	}
	
	
	//脚本运行,str表示控件位置，type表示控件行为
	private boolean runScript(String str,String textInfo,int type){
		String str1 = str;
		int totalCount = 25;
		boolean isStop = false;
		for(int count = 0;count<totalCount&&!isStop;count++){
			Log.i(TAG, "第"+(count+1)+"/"+totalCount+"次页面获取");
			AccessibilityNodeInfo node = getRootInActiveWindow();
			int i =0;
			for(i=0;i<str1.length();i++){
				int index = Integer.parseInt(str1.substring(i,i+1));
				Log.i(TAG, "index:"+index);
				Log.i(TAG,"子节点个数："+node.getChildCount());
				if(node.getChildCount()>index){
					node = node.getChild(index);
				}else{
					Log.i(TAG, "没有子节点，停止查找");
					break;
				}
			}
			String text = String.valueOf(node.getText());
			Log.i(TAG, "节点文本信息为："+text);
			if(text!=null&&i==str1.length()){//找到控件
				if(text.equals(textInfo)){
					isStop = true;
					Log.i(TAG, textInfo);
					if(type==CLICK){
						node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}
					return true;
				}
			}
			
			try {
				Log.i(TAG, "休息10s，等待下一页面出现");
				Thread.sleep(10*1000);
				
			} catch (InterruptedException e) {
				Log.i(TAG,e.toString());
				// TODO: handle exception
			}
				
		}
		Log.i(TAG, "失败");
		
		return false;
	}

	// 打开应用
	private boolean openApp(String packageString) {

		HashMap<Integer, String> appHashMap = new HashMap<Integer, String>();
		HashMap<Integer, String> packageHashMap = new HashMap<Integer, String>();
		HashMap<Integer, String> activityHashMap = new HashMap<Integer, String>();

		int size;
		int j = 0;
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String appname = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();
			String packagename = packageInfo.packageName;
			appHashMap.put(j, appname);
			packageHashMap.put(j, packagename);
			j++;
		}
		size = appHashMap.size();
		PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_ACTIVITIES);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));

		for (ResolveInfo reInfo : resolveInfos) {
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			for (int i = 0; i < size; i++) {
				if (pkgName.equals(packageHashMap.get(i))) {
					activityHashMap.put(i, activityName);
					break;
				}
			}
		}

		String activityString1 = "";
		for (int i = 0; i < appHashMap.size(); i++) {
			if (packageString.equals(packageHashMap.get(i))) {
				activityString1 = activityHashMap.get(i);
				break;
			}
		}

		try {
			Intent intent = new Intent();
			intent.setClassName(packageString, activityString1);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

			return true;
		} catch (Exception e) {
			Log.i(TAG, "应用程序启动失败：" + e.getMessage());
			Toast.makeText(this, "APP启动失败", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}
