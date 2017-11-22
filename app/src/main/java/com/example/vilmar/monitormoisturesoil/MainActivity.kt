package com.example.vilmar.monitormoisturesoil

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

    private lateinit var mValue: TextView
    private lateinit var mUsbManager: UsbManager

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }

    private fun init() {
        mValue = findViewById(R.id.tv_value_moisture)
        mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val usbDevices: HashMap<String, UsbDevice> = mUsbManager.deviceList

        if (!usbDevices.isEmpty()) {
            var keep = true

            for (entry in usbDevices.entries) {
                var device: UsbDevice = entry.value
                var deviceVID: Int = device.vendorId

                if (deviceVID == 0x2341) { //Arduino Vendor ID
                    var pi = PendingIntent.getBroadcast(this, 0,
                            Intent(ACTION_USB_PERMISSION), 0)

                    mUsbManager.requestPermission(device, pi)

                    keep = false

                }

                if (!keep) {
                    break
                }
            }
        }

    }
}
