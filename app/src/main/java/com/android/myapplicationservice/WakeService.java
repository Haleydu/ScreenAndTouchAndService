package com.android.myapplicationservice;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

public class WakeService extends Service {
    float x,y = 100;
    private final String TAG = "WakeService";
    public WakeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }
    Boolean clk = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "bright");
        wl.acquire();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<50;i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            click(0, 350);
                        }
                    }).start();
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        wl.release();
        return START_STICKY;
    }

    public void click(int a,int b){
        Log.i(TAG,"click()"+a+","+b);
        //模拟点击click事件
        x = Float.valueOf(a);
        y = Float.valueOf(b);
        Instrumentation mInst = new Instrumentation();
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                x, y, 0));
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                x, y, 0));

    }

}
