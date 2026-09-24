# Reset on Restart

Android 6.0+ Device Administrator app for an explicitly requested factory reset.

## Features
- Enable/disable awareness of Device Administrator status.
- **Reset Device Now**: confirmation dialog, then DevicePolicyManager.wipeData().
- **Restart, Then Reset**: persists an explicit reset request and performs wipeData() after BOOT_COMPLETED.

## Android 6 limitation
Android 6 (API 23) does not expose DevicePolicyManager.reboot(). A normal device-admin app cannot silently/programmatically reboot the phone. Therefore the second workflow deliberately requires the user to restart the phone using the normal power/restart controls. It never uses root, exploits, or hidden reboot mechanisms.

## LG Exalt
The project targets API 23 as its minimum. Actual factory-reset behavior depends on the LG firmware and whether it honors the standard Device Administrator wipe-data API.

## Build
GitHub Actions builds a debug APK and publishes it as a workflow artifact.
