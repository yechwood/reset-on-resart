package com.yechwood.resetonrestart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_ADMIN = 100;
    private DevicePolicyManager dpm;
    private ComponentName admin;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        dpm = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, ResetDeviceAdminReceiver.class);
        buildUi();
    }

    @Override protected void onResume() {
        super.onResume();
        if (dpm != null && admin != null) buildUi();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 36, 32, 36);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Reset on Restart");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView status = new TextView(this);
        status.setText(isAdmin() ? "Device admin: ACTIVE" : "Device admin: NOT ACTIVE");
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 24, 0, 24);
        root.addView(status, new LinearLayout.LayoutParams(-1, -2));

        Button adminButton = new Button(this);
        adminButton.setText(isAdmin() ? "Device Admin Enabled" : "Enable Device Admin");
        adminButton.setEnabled(!isAdmin());
        adminButton.setOnClickListener(v -> requestAdmin());
        root.addView(adminButton, new LinearLayout.LayoutParams(-1, -2));

        Button reset = new Button(this);
        reset.setText("RESET DEVICE NOW");
        reset.setTextSize(18);
        reset.setOnClickListener(v -> confirmImmediateReset());
        root.addView(reset, new LinearLayout.LayoutParams(-1, -2));

        Button restartReset = new Button(this);
        restartReset.setText("RESTART, THEN RESET");
        restartReset.setTextSize(18);
        restartReset.setOnClickListener(v -> confirmRestartReset());
        root.addView(restartReset, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("\nImportant: Factory reset permanently erases user data.\n\nWhen you choose \"RESTART, THEN RESET\", the reset is armed. The next time this phone restarts, this app will automatically attempt the factory reset. You must restart the phone yourself using its normal power/restart controls.");
        note.setTextSize(15);
        note.setPadding(0, 24, 0, 0);
        root.addView(note, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    private boolean isAdmin() { return dpm != null && dpm.isAdminActive(admin); }

    private void requestAdmin() {
        Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Reset on Restart needs Device Administrator permission so you can explicitly request a factory reset.");
        startActivityForResult(i, REQUEST_ADMIN);
    }

    private void confirmImmediateReset() {
        if (!isAdmin()) { requestAdmin(); return; }
        new AlertDialog.Builder(this)
            .setTitle("Factory reset?")
            .setMessage("This will permanently erase the phone's user data. This cannot be undone.")
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("RESET NOW", (d,w) -> dpm.wipeData(0))
            .show();
    }

    private void confirmRestartReset() {
        if (!isAdmin()) { requestAdmin(); return; }
        new AlertDialog.Builder(this)
            .setTitle("Restart, then reset?")
            .setMessage("When you press ARM RESET, the reset will be armed. The next time this phone restarts, this app will automatically attempt to factory-reset the phone. You must restart the phone yourself using its normal power/restart controls. The factory reset will erase the phone's user data.")
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("ARM RESET", (d,w) -> {
                getSharedPreferences(BootReceiver.PREFS, MODE_PRIVATE)
                    .edit().putBoolean(BootReceiver.PENDING, true).apply();
                new AlertDialog.Builder(this)
                    .setTitle("Reset armed")
                    .setMessage("The reset is armed. The next time this phone restarts, this app will attempt the factory reset automatically. Restart the phone now using its normal power/restart controls.")
                    .setPositiveButton("OK", null)
                    .show();
            }).show();
    }
}
