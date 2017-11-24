package com.example.vilmar.monitormoisturesoil

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var mValue: TextView
    private lateinit var mButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }

    private fun init() {
        mValue = findViewById(R.id.tv_value_moisture)
        mButton = findViewById(R.id.toggleButton)
    }

    private fun tvAppend(tv: TextView, text: CharSequence) {
        runOnUiThread { tv.append(text) }
    }
}
