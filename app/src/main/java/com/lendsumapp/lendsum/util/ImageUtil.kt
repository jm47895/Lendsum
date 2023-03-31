package com.lendsumapp.lendsum.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.util.Log

object ImageUtil {

    fun Uri.getRotateAngle(context: Context): Int {

        val inputStream =  context.contentResolver.openInputStream(this)
        var rotateDegree = 0

        try {
            val orientation = inputStream?.let {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            }?: -1

            rotateDegree = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                else -> 0
            }

            inputStream?.close()
        }catch (e: Exception){
            Log.e("ImageUtil", "Image Orientation error: $e")
            inputStream?.close()
        }

        return rotateDegree
    }

    fun Bitmap.rotateBitmap(angle: Float): Bitmap
    {
        val matrix = Matrix()
        matrix.preRotate(angle);
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true);
    }

}