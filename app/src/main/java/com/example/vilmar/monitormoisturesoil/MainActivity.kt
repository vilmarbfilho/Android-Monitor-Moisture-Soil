package com.example.vilmar.monitormoisturesoil

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.vilmar.monitormoisturesoil.settings.SettingsActivity


class MainActivity : AppCompatActivity() {

    private lateinit var mValue: TextView
    private lateinit var mSettingsButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }

    private fun init() {
        mValue = findViewById(R.id.tv_main_value_moisture)
        mSettingsButton = findViewById(R.id.ib_main_settings)

        mSettingsButton.setOnClickListener {
            startActivity(Intent(SettingsActivity.newIntent(this)))
        }
    }

    private fun tvAppend(tv: TextView, text: CharSequence) {
        runOnUiThread { tv.append(text) }
    }
}
