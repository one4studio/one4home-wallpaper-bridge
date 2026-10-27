// Run on a worker thread, as part of your EXISTING successful apply flow.
// croppedImage is your exact final Home image, including crop and orientation.
val bridgeDirectory = File(context.cacheDir, "one4home-bridge").apply { mkdirs() }
val appliedFile = File.createTempFile("applied-", ".jpg", bridgeDirectory)
croppedImage.copyTo(appliedFile, overwrite = true)

// For Home + Lock use FLAG_SYSTEM or FLAG_LOCK. Never notify for Lock only.
val wallpaperId = appliedFile.inputStream().use { stream ->
    WallpaperManager.getInstance(context).setStream(
        stream, null, true, WallpaperManager.FLAG_SYSTEM
    )
}

if (wallpaperId > 0 && preferences.getBoolean("one4home_connected", false)) {
    // Bridge failure must not turn an already successful wallpaper apply into an error.
    runCatching {
        One4HomeWallpaperBridge.notifyApplied(
            context, appliedFile, wallpaperId, mimeType = "image/jpeg"
        )
    }
}
// Keep this uniquely named file after dispatch: the receiver runs asynchronously.
// During your existing cache cleanup (e.g. after 24 hours), first call
// One4HomeWallpaperBridge.revoke(context, appliedFile), then delete the file.
// Do not revoke or delete it in a finally block immediately after dispatch.
