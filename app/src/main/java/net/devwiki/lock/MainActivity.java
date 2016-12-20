package net.devwiki.lock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

    private static final int REQUEST_ADMIN_CODE = 0x001;

    private DevicePolicyManager policyManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);

        if (policyManager.isAdminActive(componentName)) {
            lockScreen();
        } else {
            activeManage();
        }
    }

    private void lockScreen() {
        policyManager.lockNow();
        finish();
    }

    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_hint));

        startActivityForResult(intent, REQUEST_ADMIN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADMIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                lockScreen();
            } else {
                showNotPermissionHint();
            }
        }
    }

    private void showNotPermissionHint() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.no_permission_title)
                .setMessage(R.string.no_permission_msg)
                .setCancelable(false)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activeManage();
                    }
                })
                .create();
        dialog.show();
    }
}
