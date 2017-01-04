package com.softsec.thread;

import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;

public class ScreenShotThread extends Thread
{
	public String picSavePath;
	public static boolean isScreenShotThreadOver = false;

	public ScreenShotThread(String picSavePath)
	{
		this.picSavePath = picSavePath;
	}

	public void run()
	{
		try 
		{
			isScreenShotThreadOver = false;
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("su\n");
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            os.writeBytes("/system/bin/screencap /sdcard/" + picSavePath + "\n");
			os.writeBytes("exit \n");
			Log.i("Record", "picSavePath=" + picSavePath);
			Thread.sleep(10*1000);
			int ev=proc.waitFor();
			os.flush(); 
			os.close();
			Log.i("Record", "截图完成:" + ev);
				
			
			isScreenShotThreadOver=true;
//						if(proc!=null){
//							proc.exitValue();
//							proc.destroy();}
		} 
		catch (Exception e) 
		{
			Log.i("Record","Exception:",e);
			Message msg1 = new Message();
			msg1.obj = e.toString();
			isScreenShotThreadOver=true;
		}
	}
}
