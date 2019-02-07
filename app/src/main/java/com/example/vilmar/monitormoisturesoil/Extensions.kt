package com.example.vilmar.monitormoisturesoil

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by vilmar on 24/11/17.
 */

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun Context.toastIt(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}