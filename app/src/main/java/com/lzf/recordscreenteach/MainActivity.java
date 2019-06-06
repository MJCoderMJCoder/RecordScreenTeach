package com.lzf.recordscreenteach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by MJCoder on 2019-06-06.
 */

public class MainActivity extends Activity {
    private TextView sample_text;

    private int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sample_text = findViewById(R.id.sample_text);
        sample_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击位置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new File("/data/data/com.qwh.grapebrowser").exists()) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.qwh.grapebrowser");
            startActivity(launchIntent);
            //            Intent local = new Intent();
            //            local.setAction("android.intent.action.MAIN");
            //            local.addCategory("android.intent.category.HOME");
            //            startActivity(local);
            if (ClickService.isRunning()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent("auto.click");
                        intent.putExtra("flag", 1);
                        intent.putExtra("resIdOrText", "我的");
                        sendBroadcast(intent);
                    }
                }, 1500);
            } else {
                openAccessibilityServiceSettings();
            }
        } else {
            Toast.makeText(this, "请先安装葡萄浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 传入在屏幕中的比例位置，坐标左上角为基准
     *
     * @param act    传入Activity对象
     * @param ratioX 需要点击的x坐标在屏幕中的比例位置
     * @param ratioY 需要点击的y坐标在屏幕中的比例位置
     */
    public void autoClickRatio(Activity act, final double ratioX, final double ratioY) {
        width = this.getWindowManager().getDefaultDisplay().getWidth();
        height = this.getWindowManager().getDefaultDisplay().getHeight();
        Log.v("" + width, "" + height);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 线程睡眠0.3s
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 生成点击坐标
                int x = (int) (width * ratioX);
                int y = (int) (height * ratioY);

                // 利用ProcessBuilder执行shell命令
                String[] order = {"input", "tap", "" + x, "" + y};
                try {
                    new ProcessBuilder(order).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "找 【RecordScreenTeach】 ,然后开启服务", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}