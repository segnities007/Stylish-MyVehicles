package com.segnities007.stylish_myvehicles.presentation.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** カメラ撮影用の一時画像UriをFileProvider経由で作成する共通ヘルパー。 */
object ImageCaptureHelper {
    fun createImageUri(context: Context, prefix: String): Uri {
        val dir = File(context.cacheDir, "captured_images").apply { mkdirs() }
        val file = File.createTempFile("${prefix}_", ".jpg", dir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
