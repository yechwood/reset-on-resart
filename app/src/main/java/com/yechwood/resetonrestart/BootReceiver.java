package com.yechwood.resetonrestart;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    static final String PREFS = "reset_state";
    static final String PENDING = "pending_reset";

    @Override public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        android.content.SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(PENDING, false)) return;

        DevicePolicyManager dpm =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        android.content.ComponentName admin =
                new android.content.ComponentName(context, ResetDeviceAdminReceiver.class);

        if (dpm != null && dpm.isAdminActive(admin)) {
            prefs.edit().putBoolean(PENDING, false).apply();
            dpm.wipeData(0);
        }
    }
}
