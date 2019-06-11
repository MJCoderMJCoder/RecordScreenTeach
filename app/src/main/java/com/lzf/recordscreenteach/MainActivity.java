package com.lzf.recordscreenteach;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
        MyApplication.mainActivity = this;
        sample_text = findViewById(R.id.sample_text);
        sample_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击位置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(this);
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            Intent intent = mediaProjectionManager.createScreenCaptureIntent();
            PackageManager packageManager = this.getPackageManager();
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                //存在录屏授权的Activity
                this.startActivityForResult(intent, 6003);
            } else {
                Toast.makeText(this, "该机型不支持录屏功能", Toast.LENGTH_SHORT).show();
            }
        }
        if (new File("/data/data/com.xinhe.sdb").exists()) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.xinhe.sdb");
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
            Toast.makeText(this, "请先安装Fitmind", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * app targetApi 在 6.0 以上时，还需要动态获取权限。
     *
     * @param activity
     */
    private static void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //动态申请
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
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

    private void setUpMediaRecorder() {
        String mRecordFilePath = FileUtil.getFile(this, "RecordScreen", null) + File.separator + System.currentTimeMillis() + ".mp4";
        MediaRecorder mMediaRecorder = new MediaRecorder();
        //设置音频来源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频来源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //输出的录屏文件格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //录屏文件路径
        mMediaRecorder.setOutputFile(mRecordFilePath);
        //视频尺寸
        mMediaRecorder.setVideoSize(ScreenUtils.getScreenW(this), ScreenUtils.getScreenH(this));
        //音视频编码器
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //比特率
        mMediaRecorder.setVideoEncodingBitRate((int) (ScreenUtils.getScreenW(this) * ScreenUtils.getScreenH(this) * 3.6));
        //视频帧率
        mMediaRecorder.setVideoFrameRate(20);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6003 && resultCode == Activity.RESULT_OK) {
            try {
                setUpMediaRecorder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "拒绝录屏", Toast.LENGTH_SHORT).show();
        }
    }
}