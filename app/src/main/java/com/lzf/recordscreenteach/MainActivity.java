package com.lzf.recordscreenteach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

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
        MyApplication.mainActivity = this;
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
                        intent.putExtra("flag", 2);
                        intent.putExtra("resIdOrText", "我的");
                        sendBroadcast(intent);
                    }
                }, 3000);
            } else {
                openAccessibilityServiceSettings();
            }
        } else {
            Toast.makeText(this, "请先安装葡萄浏览器", Toast.LENGTH_SHORT).show();
        }
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