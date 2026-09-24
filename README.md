# Reset on Restart

Android 6.0+ Device Administrator app for an explicitly requested factory reset.

## Features
- Enable Device Administrator permission.
- **Reset Device Now**: confirmation dialog, then `DevicePolicyManager.wipeData()`.
- **Restart, Then Reset**: arms a reset request and performs `wipeData()` after the phone's next normal boot completes.

## How “Restart, Then Reset” works

1. Press **RESTART, THEN RESET**.
2. Confirm **ARM RESET**.
3. The app saves the reset request.
4. Restart the phone normally using its power/restart controls.
5. After the phone finishes booting, the app receives the boot-completed event and automatically attempts the factory reset.

The reset is **not immediate** when you press the button. The button arms the reset for the phone's **next restart**.

## Important

A factory reset permanently erases the phone's user data and cannot be undone. The app does not silently restart the phone; you restart it yourself.

## LG Exalt

The project targets API 23 as its minimum. Actual factory-reset behavior depends on the LG firmware and whether it honors the standard Device Administrator wipe-data API.

## Build

GitHub Actions builds a debug APK and publishes it as a workflow artifact.
