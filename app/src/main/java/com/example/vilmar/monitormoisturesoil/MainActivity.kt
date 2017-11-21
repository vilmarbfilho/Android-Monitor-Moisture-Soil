package com.example.vilmar.monitormoisturesoil

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.physicaloid.lib.Physicaloid


class MainActivity : AppCompatActivity() {

    private lateinit var mValue: TextView

    private lateinit var mPhysicaloid: Physicaloid

    private val mBaudrate: Int = 9600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }

    private fun init() {
        mValue = findViewById(R.id.tv_value_moisture)

        mPhysicaloid = Physicaloid(this)
        mPhysicaloid.setBaudrate(mBaudrate)

        if (mPhysicaloid.open()) {
            mPhysicaloid.addReadListener { size ->
                val buf = ByteArray(size)
                mPhysicaloid.read(buf, size)
                mValue.text = buf.toString()
            }
        }
    }
}
