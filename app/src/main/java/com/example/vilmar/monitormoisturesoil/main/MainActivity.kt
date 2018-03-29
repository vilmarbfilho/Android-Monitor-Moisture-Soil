package com.example.vilmar.monitormoisturesoil.main

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.vilmar.monitormoisturesoil.settings.SettingsActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.util.concurrent.Executors
import com.hoho.android.usbserial.util.SerialInputOutputManager
import android.widget.Toast
import com.example.vilmar.monitormoisturesoil.AppApplication
import com.example.vilmar.monitormoisturesoil.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val BAUD_RATE: Int = 115200
    private val DATA_BITS: Int = 8

    private var mUsbSerialPort: UsbSerialPort? = null
    private var  mSerialIoManager: SerialInputOutputManager? = null

    private val mExecutor = Executors.newSingleThreadExecutor()


    private val mListener = object : SerialInputOutputManager.Listener {

        override fun onRunError(e: Exception) {
            toastIt("Runner stopped.")
        }

        override fun onNewData(data: ByteArray) {
            this@MainActivity.runOnUiThread({ this@MainActivity.updateReceivedData(data) })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActivity()

    }

    private fun setupActivity() {
        ibMainSettings.setOnClickListener {
            startActivity(Intent(SettingsActivity.newIntent(this)))
        }
    }

    override fun onResume() {
        super.onResume()
        setupUSB()
    }

    private fun setupUSB() {
        mUsbSerialPort = AppApplication.sPort

        if (mUsbSerialPort != null) {
            val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            val connection: UsbDeviceConnection = usbManager.openDevice(mUsbSerialPort?.driver?.device)

            if (connection == null) {
                toastIt("Opening device failed")
                return
            }

            mUsbSerialPort?.open(connection)
            mUsbSerialPort?.setParameters(BAUD_RATE, DATA_BITS, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        } else {
            toastIt("No serial device.")
        }

        onDeviceStateChange()
    }

    override fun onPause() {
        super.onPause()
        stopIoManager()
        if (mUsbSerialPort != null) {
            try {
                mUsbSerialPort?.close()
            } catch (e: IOException) {
                // Ignore.
            }
        }
    }

    private fun stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager?.stop()
        }
    }

    private fun startIoManager() {
        if (mUsbSerialPort != null) {
            mSerialIoManager = SerialInputOutputManager(mUsbSerialPort, mListener)
            mExecutor.submit(mSerialIoManager)
        }
    }

    private fun updateReceivedData(data: ByteArray) {
        //toastIt("Lenght: ${data.size} = ${String(data)}")
        if (data.size > 2) {
            updateText(String(data).trim().toInt())
        }
    }

    private fun onDeviceStateChange() {
        stopIoManager()
        startIoManager()
    }

    private fun updateText(state: Int?) {
        runOnUiThread {
            if (state != null) {
                tvMainValueMoisture.text = getStateAsString(state)
            }
        }
    }

    private fun toastIt(message: String?) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun getStateAsString(state: Int) : String {
        return when(state) {
            0 -> "SOLO SECO"
            1 -> "UMIDADE MODERADA"
            2 -> "SOLO ÚMIDO"
            else -> "NÃO FOI POSSÍVEL DETECTAR A UMIDADE DO SOLO"
        }
    }
}
