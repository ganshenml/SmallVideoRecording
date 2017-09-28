package com.dalong.smallvideorecording;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dalong.recordlib.RecordVideoActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int PERMISSION_CAMERA_OP_STATE = 1000;
    private String videoPath;

    public static final int TAKE_DATA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermission();

        File path = new File(Environment.getExternalStorageDirectory(),
                "videodemo");
        if (!path.exists()) {
            path.mkdirs();
        }
        videoPath = path.getAbsolutePath() + File.separator + "demo.mp4";

    }

    /**
     * 录制
     *
     * @param view
     */
    public void doRecording(View view) {
        Log.e(TAG,"doRecording");
        Intent intent = new Intent(this, RecordVideoActivity.class);
        intent.putExtra(RecordVideoActivity.RECORD_VIDEO_PATH, videoPath);
        startActivityForResult(intent, TAKE_DATA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_DATA:
                if (resultCode == RecordVideoActivity.TAKE_VIDEO_CODE) {
                    String videoPath = data.getStringExtra(RecordVideoActivity.TAKE_VIDEO_PATH);
                    Toast.makeText(this, "视频路径：" + videoPath, Toast.LENGTH_SHORT).show();
                } else if (resultCode == RecordVideoActivity.TAKE_PHOTO_CODE) {
                    String photoPath = data.getStringExtra(RecordVideoActivity.TAKE_PHOTO_PATH);
                    Toast.makeText(this, "图片路径：" + photoPath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void askPermission() {
        Log.d(TAG,"askPermission");
        AndPermission.with(this)
                .requestCode(PERMISSION_CAMERA_OP_STATE)
                .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                        Log.d(TAG,"showRequestPermissionRationale");
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .callback(listener)
                .start();
    }


    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            Log.v(TAG,"PERMISSION_CAMERA_OP_STATE 申请成功");
            // 权限申请成功回调。
            if (requestCode == PERMISSION_CAMERA_OP_STATE) {
                Log.d(TAG,"PERMISSION_CAMERA_OP_STATE 申请成功");
            } else if (requestCode == 101) {
                // TODO 相应代码。
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            Log.d(TAG,"onFailed 申请失败");

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, 101).show();
            }

        }
    };
}
