package com.leafy.shared.ui.utils

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, message: String, delayTime: Int = Toast.LENGTH_LONG){
    Toast.makeText(context, message, delayTime).show()
}