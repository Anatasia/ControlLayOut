package com.softsec.thread;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * Created by gaoyuhao on 15/8/20.
 */
public class StreamGobbler extends Thread {
    InputStream is;
    String type;

    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null){
                Log.i("Record", "StreamGobbler[" + type + "]:" + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.i("Record", "Error", ioe);
        }
    }
}