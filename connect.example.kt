// In an AndroidX ComponentActivity. Register before the Activity is started.
private val connectOne4Home = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        // Remember the opt-in locally; offer a disconnect action in your app.
        // Do not reconnect automatically or repeatedly show the consent prompt.
        preferences.edit().putBoolean("one4home_connected", true).apply()
    }
}

// Run only when the user taps your "Connect to One4Home" button.
fun connectToOne4Home() {
    try {
        connectOne4Home.launch(One4HomeWallpaperBridge.connectionIntent())
    } catch (_: ActivityNotFoundException) {
        // Show an install/update option for One4Home 0.5.80 (392) or newer.
    }
}
