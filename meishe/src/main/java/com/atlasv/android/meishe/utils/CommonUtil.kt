package com.atlasv.android.meishe.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


/**
 * Created by woyanan on 2021/7/9
 */
object CommonUtil {
    const val PATH = "path"

    fun contentUri2FilePath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        var filePath: String? = null
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
            if (cursor?.moveToFirst() == true) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                filePath = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            //no-op
        } finally {
            cursor?.close()
        }
        return filePath
    }

}