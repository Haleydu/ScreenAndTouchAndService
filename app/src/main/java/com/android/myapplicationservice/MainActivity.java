package com.android.myapplicationservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity{
    private final String TAG = "Screen_MainActivity";

    private TextView tv_count;
    private Button btn_test;
    private Button btn_screenshot;
    private Button btn_startservice;
    private Button btn_stopservice;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_count = findViewById(R.id.tv_count);
        btn_test = findViewById(R.id.btn_test);
        btn_screenshot = findViewById(R.id.btn_screenshot);
        btn_startservice = findViewById(R.id.btn_startService);
        btn_stopservice = findViewById(R.id.btn_stopService);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "bright");
//        wl.acquire();
//        wl.release();
        //
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_count.setText("" + (count++));
            }
        });


        btn_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //screenOff();
                //开始截屏
                ScreenShot("/mnt/sdcard/DCIM/1.png");
            }
        });

        //开始服务
        btn_startservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Service = new Intent(MainActivity.this, WakeService.class);
                startService(intent_Service);
            }
        });

        //关闭服务
        btn_stopservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, WakeService.class));
            }
        });
    }

    private void ScreenShot(final String filename){
        Log.d(TAG,"DoScreencap");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = false;
                String[] args = {"screencap", "-p", filename};
                Log.d(TAG,"filepath:" + filename);
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                Process process = null;
                BufferedReader successResult = null;
                BufferedReader errorResult = null;
                StringBuilder successMsg = new StringBuilder();
                StringBuilder errorMsg = new StringBuilder();
                try {
                    process = processBuilder.start();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String s;
                    while ((s = successResult.readLine()) != null) {
                        successMsg.append(s);
                        Log.d(TAG,"successMsg.append(s);");
                        ret = true;
                    }
                    while ((s = errorResult.readLine()) != null) {
                        errorMsg.append(s);
                        Log.d(TAG,"errorMsg.append(s);");
                        ret = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"IOException");
                    //return faile;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG,"Exception");
                } finally {
                    try {
                        if (successResult != null) {
                            successResult.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (process != null) {
                        process.destroy();
                    }
                    if(ret){
                        Log.d(TAG,"Screencap complete, return:success ");
                        //return true;
                    }else{
                        Log.d(TAG,"Screencap false, return:false");
                        //return false;
                    }
                }
            }
        }).start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int[] temp = new int[2];
        btn_test.getLocationOnScreen(temp);
        btn_test.setText("Test (" + temp[0] + "," + temp[1] + ")");

        btn_startservice.getLocationOnScreen(temp);
        btn_startservice.setText("startservice (" + temp[0] + "," + temp[1] + ")");

        btn_stopservice.getLocationOnScreen(temp);
        btn_stopservice.setText("stopservice (" + temp[0] + "," + temp[1] + ")");

    }


    private void screenOff(){
        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(MainActivity.this, MyAdmin.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            //isScreenOn = false;
            policyManager.lockNow();
        } else {
            Toast.makeText(this,"没有设备管理权限", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            // 指定动作名称
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 指定给哪个组件授权
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
            startActivity(intent);
        }

    }

}
