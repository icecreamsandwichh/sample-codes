// Save wallpaper to external storage on devices running android Q and higher, accessible to other apps
@RequiresApi(Build.VERSION_CODES.Q)
suspend fun Context.saveWallpaperQ(wallpaper: Wallpaper): Result<Boolean?> {
    return withContext(Dispatchers.IO) {
        val filename = "${wallpaper.name}.jpg"
        val filesDir = Environment.DIRECTORY_PICTURES + "/WallpaperX"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.MediaColumns.RELATIVE_PATH, filesDir)

        }
        val bmp = getBitmap(wallpaper.imageUri)
        val imageUri: Uri? =
            this@saveWallpaperQ.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        val result = kotlin.runCatching {
            val fos: OutputStream? = imageUri?.let { contentResolver.openOutputStream(it) }
            fos?.use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }.onFailure {
            Log.e("ApplicationError/Extensions", "saveWallpaperQ: $it")
        }
        result
    }
}
