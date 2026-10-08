# TeamsGo

A lightweight **Microsoft Teams** app for old Android phones (Android 8.0+), built as a WebView wrapper around Teams web.

## Download

### TeamsGo (one APK for all phones)
[**Download TeamsGo**](https://drive.google.com/drive/folders/1pVIF0bW10Vgntvohsw23lkkXV-If4LIA)

The folder has the APK and a picture guide. No CPU check is needed, because this one file fits every phone.

### Official Microsoft Teams for Android 8 (APKMirror builds)
Version `1416/1.0.0.2025225102`, minimum Android 8.0, DPI `nodpi`.

| Architecture | Who it is for | Download |
|---|---|---|
| arm64-v8a | Most phones since about 2017. **Try this first.** | [Download](https://drive.google.com/drive/folders/14JbxBylPaBUj8G0OpG9mAaBveLwfpHBJ) |
| armeabi-v7a | Older 32-bit phones | [Download](https://drive.google.com/drive/folders/1KXKJ3uRLuDPTlVz4zIcTIPxgvRnZq4Nm) |
| x86_64 | Emulators, some Intel devices | [Download](https://drive.google.com/drive/folders/1kK5dg_7sI31Yt5SZhWDmRbAKzfMeZGzC) |
| x86 | Emulators, old Intel tablets | [Download](https://drive.google.com/drive/folders/1zWWojcr0L7EA7k_Rhv9Bc3xFriVeC6qd) |

Each folder has the APK and a picture guide.

## Which file is mine?
1. **Android version:** Settings > About phone > Android version. It must be 8.0 or higher.
2. **Easy rule:** try **arm64-v8a** first. If Android says "App not installed" or "not compatible", try **armeabi-v7a**.
3. **To be 100% sure:** install the free app *Droid Hardware Info*, open the System tab and read "Instruction sets".
4. Do **not** use BUNDLE or `.apkm` files. They need Android 11 or newer.

## Install
1. Open the `.apk` file and allow "Install unknown apps" if Android asks.
2. Tap Install, open the app, and sign in with your Microsoft account.
3. For calls: 3-dot menu, tick **Allow microphone** (and **Allow camera**), then tap **Reload**.

## Features
- Teams in its own app, with a purple status bar and navigation bar and a loading line
- Login and cache are saved between launches
- Microphone and camera switches, **off by default**
- Hide popups: tooltips, tips and "join without audio" boxes
- Dark site switch
- Save in background: keeps calls alive with the screen off
- File attach and download
- Offline screen with a Try again button
- Teams links open inside the app

## Limits
- It is a WebView app, so it is about as fast as Chrome on your phone
- No push notifications
- Keep **Android System WebView** updated in the Play Store

## Build
Open the project in Code on the Go (or Android Studio) and run `:app:assembleDebug`. The APK is created in `app/build/outputs/apk/debug/`.

## Credits
Made by **Uzair Nawaz**. Not affiliated with Microsoft. Microsoft Teams is a trademark of Microsoft.
