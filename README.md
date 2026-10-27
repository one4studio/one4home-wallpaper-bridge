# One4Home Wallpaper Bridge — integration kit v1.0.3

For Android wallpaper-app developers. Works with production One4Home 0.5.80 (392) and newer. This kit is a source integration, not a published Maven dependency. The helper is the exact source shipped with One4Home 0.5.80 (392). MIT-licensed; no fee, account, API key, backend, service or polling required.

## What it does

After your app successfully sets a static Home wallpaper, share the exact applied image with One4Home so its Glass and Acrylic surfaces can sample the same picture. Your app still applies the wallpaper using WallpaperManager. It does not launch Home to apply it, replace a live wallpaper service, or bypass One4Home Pro features. Live/video wallpapers and Lock-only applies are outside this protocol.

## Setup

1. Copy One4HomeWallpaperBridge.kt into your app, keeping its package or updating your imports. It uses AndroidX Core's FileProvider; use your existing compatible androidx.core:core-ktx dependency (the tested library uses 1.17.0). The helper supports API 23+, but the setStream example requires API 24+. One4Home itself requires Android 10/API 29+.
2. Merge AndroidManifest.example.xml into your existing manifest. Do not replace your application manifest. SET_WALLPAPER is the normal permission for your existing apply flow. Keep the queries package so Android can resolve One4Home when granting URI access.
3. Save one4home_bridge_paths.xml in app/src/main/res/xml/. Expose only cacheDir/one4home-bridge/, never the whole files/cache directory. Keep the provider authority exactly ${applicationId}.one4home.bridge.
4. Add a user-initiated Connect to One4Home action using connect.example.kt. The example assumes your Activity already has a SharedPreferences named preferences; import the Android/AndroidX Activity Result classes and com.one4studio.wallpaperbridge.One4HomeWallpaperBridge. Register before the Activity reaches STARTED. Ordinary startActivity does not provide a verified result caller and is rejected.
5. Integrate apply.example.kt into your existing worker-thread apply flow. It assumes context, preferences and croppedImage: File exist. Do not set the wallpaper twice. Use the ID returned by your successful Home setStream call, not a guessed current ID. For PNG, preserve the PNG bytes and use .png / image/png.

## Consent and revocation

The user explicitly approves your verified application name in One4Home. Access is bound to your installed signing certificate. Cancelled connections must not enable sharing. Offer a disconnect action in your wallpaper app that disables further sharing. No One4Home settings page is required. Your local connected preference is not proof that access remains granted; revoked or stale imports are ignored. Offer reconnect only when the user requests it. A changed signing certificate needs fresh consent.

## Correct image and lifetime

Send the exact Home image with the final crop/orientation, not an uncropped original, thumbnail or preview. Do not modify or reuse the shared file. Use a unique file for every apply. sendBroadcast confirms dispatch only: there is no import-complete acknowledgement. Keep files long enough for delivery (for example 24 hours). Your existing cache cleanup should call revoke for each expired image/depth file before deleting it. The receiver makes its own bounded private copy. Images above 64 MiB are rejected. Do not send persistable URI permissions. Android may clear cache earlier; missed delivery should not fail normal wallpaper application.

## Optional depth

notifyApplied accepts depth: File? for a matching transparent PNG in the same narrow provider directory. It must have the same dimensions, crop and orientation as the applied picture. Invalid layers are rejected. Sharing a layer does not enable or unlock a Pro feature. Apply cleanup and revoke to both files.

## Verify the integration

- With One4Home absent or older, Connect offers install/update and normal wallpaper application still works.
- Cancel consent: no sharing preference is enabled. Approve: apply a distinctive static Home image and check Glass/Acrylic match when returning to Home.
- Apply Home + Lock: notify once for the Home ID. Apply Lock only: do not notify.
- Disconnect in your wallpaper app: normal wallpaper application continues, with no bridge imports sent.
- Quickly apply two different images: the newest image wins; old wallpaper IDs are ignored.
- Verify crop and rotation on phones, foldable cover/inner displays and landscape; OEM wallpaper scaling may differ.
- For a debug One4Home build, add its debug package to your debug manifest queries and pass homePackage = "com.one4studio.one4home.debug" to connectionIntent, notifyApplied and revoke. Production uses the default package.

The helper and real FileProvider grant/import flow were verified in One4Home's native integration fixture on a disposable Android emulator. This is not a claim of device-by-device verification for your app.

Documentation: https://one4home.app/developers/wallpaper-bridge
Support: support.one4studio@gmail.com
Android references: https://developer.android.com/reference/android/app/WallpaperManager and https://developer.android.com/reference/androidx/core/content/FileProvider

## Versions and compatibility reports

Download a tagged source kit from [Releases](https://github.com/one4studio/one4home-wallpaper-bridge/releases). The integration protocol is unchanged in 1.0.3; this version adds the public repository and corrects the obsolete settings-page test instruction. The helper source remains compatible with One4Home 0.5.80 (392)+.

Report reproducible integration problems through [GitHub Issues](https://github.com/one4studio/one4home-wallpaper-bridge/issues). Include kit and One4Home versions, your public app/package, Android version, device, static Home/Home+Lock target, and redacted reproduction steps. Do not attach private wallpapers, signing keys, user data or unredacted logs. For private reports email support.one4studio@gmail.com.

Live-frame streaming is not supported by this protocol. Never invent a wallpaper ID or repeatedly send live frames.
