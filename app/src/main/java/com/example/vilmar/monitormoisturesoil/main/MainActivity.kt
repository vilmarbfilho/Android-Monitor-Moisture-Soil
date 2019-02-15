package com.example.vilmar.monitormoisturesoil.main

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.vilmar.monitormoisturesoil.settings.SettingsActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.util.concurrent.Executors
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.example.vilmar.monitormoisturesoil.AppApplication
import com.example.vilmar.monitormoisturesoil.R
import com.example.vilmar.monitormoisturesoil.toastIt
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val requestCodeMain = 1
    private val baudRate = 115200
    private val dataBits = 8

    private val justDry = 0
    private val moderateHumidity = 1
    private val moistSoil = 2

    private var usbSerialPort: UsbSerialPort? = null
    private var  serialIoManager: SerialInputOutputManager? = null

    private val executor = Executors.newSingleThreadExecutor()

    private val listener = object : SerialInputOutputManager.Listener {

        override fun onRunError(e: Exception) {
            toastIt("Runner stopped.")
        }

        override fun onNewData(data: ByteArray) {
            this@MainActivity.runOnUiThread {
                this@MainActivity.updateReceivedData(data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActivity()
    }

    private fun setupActivity() {
        ibMainSettings.setOnClickListener {
            startActivityForResult(Intent(SettingsActivity.newIntent(this)), requestCodeMain)
        }
    }

    override fun onResume() {
        super.onResume()
        setupUSB()
    }

    private fun setupUSB() {
        usbSerialPort = AppApplication.usbSerialPort

        if (usbSerialPort != null) {
            val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            val connection: UsbDeviceConnection = usbManager.openDevice(usbSerialPort?.driver?.device)

            if (connection == null) {
                toastIt("Opening device failed")
                return
            }

            usbSerialPort?.open(connection)
            usbSerialPort?.setParameters(baudRate, dataBits, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        } else {
            toastIt("No serial device.")
        }

        onDeviceStateChange()
    }

    override fun onPause() {
        super.onPause()
        stopIoManager()
        if (usbSerialPort != null) {
            try {
                usbSerialPort?.close()
            } catch (e: IOException) {
                // Ignore.
            }
        }
    }

    private fun stopIoManager() {
        if (serialIoManager != null) {
            serialIoManager?.stop()
        }
    }

    private fun startIoManager() {
        if (usbSerialPort != null) {
            serialIoManager = SerialInputOutputManager(usbSerialPort, listener)
            executor.submit(serialIoManager)
        }
    }

    private fun updateReceivedData(data: ByteArray) {
        //toastIt("Lenght: ${data.size} = ${String(data)}")
        if (data.size > 2) {
            val drawable = getSmileDrawableByState(String(data).trim().toInt())
            updateSmileImage(drawable)
        }
    }

    private fun onDeviceStateChange() {
        stopIoManager()
        startIoManager()
    }

    private fun getSmileDrawableByState(state : Int): Drawable {
        return when(state) {
            // just dry
            justDry -> getDrawable(R.drawable.ic_smile_bad)

            // moderate humidity
            moderateHumidity -> getDrawable(R.drawable.ic_smile_normal)

            // moist soil
            moistSoil -> getDrawable(R.drawable.ic_smile_happy)

            // do not detected reading
            else -> getDrawable(R.drawable.ic_smile_normal)
        }
    }

    private fun updateSmileImage(drawable : Drawable) {
        runOnUiThread {
            ivSmile.setImageDrawable(drawable)
        }
    }

}
