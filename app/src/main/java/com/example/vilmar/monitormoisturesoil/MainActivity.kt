package com.example.vilmar.monitormoisturesoil

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.vilmar.monitormoisturesoil.settings.SettingsActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.util.concurrent.Executors
import com.hoho.android.usbserial.util.SerialInputOutputManager
import android.widget.Toast
import java.io.IOException
import com.hoho.android.usbserial.util.HexDump



class MainActivity : AppCompatActivity() {

    private val BAUD_RATE: Int = 115200
    private val DATA_BITS: Int = 8

    private lateinit var mValue: TextView
    private lateinit var mSettingsButton: ImageView

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
        mValue = findViewById(R.id.tv_main_value_moisture)
        mSettingsButton = findViewById(R.id.ib_main_settings)

        mSettingsButton.setOnClickListener {
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
        val message = ("Read " + data.size + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n")

        updateText(message)
    }

    private fun onDeviceStateChange() {
        stopIoManager()
        startIoManager()
    }

    private fun updateText(text: CharSequence) {
        runOnUiThread {
            mValue.text = text
        }
    }

    private fun toastIt(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}
