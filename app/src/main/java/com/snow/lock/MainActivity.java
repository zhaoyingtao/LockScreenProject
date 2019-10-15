package com.snow.lock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lock();
    }

    //锁屏
    private void lock() {
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        // 判断该组件是否有系统管理员的权限
        boolean isAdminActive = dpm.isAdminActive(componentName);

        if (!isAdminActive) {//这一句一定要有...
            Intent intent = new Intent();
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 101);
        }
        //isAdminActive 为false，直接调用dpm.lockNow()会报java.lang.SecurityException: No active admin owned by uid 10510 for policy #3
        if (isAdminActive) {
            dpm.lockNow();
            //杀掉进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //这种方式可能会造成自动重启
//        android.os.Process.killProcess(android.os.Process.myPid());
        //授权技术关闭应用
        finish();
    }
}
