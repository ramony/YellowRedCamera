package com.raymond.yellowredcamera.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun saveToFile(context: Context, folder:String, bitmap: Bitmap?) {
    try {
        val fileSaveDirectory = File(context.getExternalFilesDir(null), folder)
        if (!fileSaveDirectory.exists()) {
            fileSaveDirectory.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpeg"
        val saveFile = File(fileSaveDirectory, fileName)

        val outputStream = FileOutputStream(saveFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        moveToAlbum(context, folder, saveFile)
        Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show()
    }
}

private fun moveToAlbum(context: Context,folder:String, file: File): Boolean {
    val contentResolver = context.contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*") // 这里是 image/*
        values.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator + folder
        )
        val contentResolver: ContentResolver = contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false
        try {
            val outputStream = contentResolver.openOutputStream(uri) ?: return false
            val fileInputStream = FileInputStream(file)
            FileUtils.copy(fileInputStream, outputStream)
            fileInputStream.close()
            outputStream.close()
            file.delete()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    } else {
        val cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!cameraDir.exists()) {
            cameraDir.mkdirs()
        }
        val sicilyDirPath = cameraDir.absolutePath + File.separator + folder
        val sicilyDir = File(sicilyDirPath)
        if (!sicilyDir.exists()) {
            sicilyDir.mkdirs()
        }
        val finalPath = sicilyDir.absolutePath + File.separator + file.name
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        values.put(MediaStore.Images.Media.DATA, finalPath)
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false
        try {
            val outputStream = contentResolver.openOutputStream(uri) ?: return false
            val fileInputStream = FileInputStream(file)
            FileUtils.copy(fileInputStream, outputStream)
            fileInputStream.close()
            outputStream.close()
            val outputFile = File(finalPath)
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.SIZE, outputFile.length())
            contentResolver.update(uri, contentValues, null, null)
            // 通知媒体库更新
            val intent = Intent(@Suppress("DEPRECATION") Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
            context.sendBroadcast(intent)
            file.delete()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}