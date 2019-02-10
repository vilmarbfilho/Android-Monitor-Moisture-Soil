package com.example.vilmar.monitormoisturesoil.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.vilmar.monitormoisturesoil.R
import com.example.vilmar.monitormoisturesoil.settings.adapter.USBDeviceAdapter
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.example.vilmar.monitormoisturesoil.AppApplication
import com.example.vilmar.monitormoisturesoil.settings.DeviceAsyncTask.OnDeviceUpdate
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by vilmar on 24/11/17.
 */

class SettingsActivity : AppCompatActivity() {

    private val messageRefresh = 101
    private val refreshTimeoutMillis = 5000L

    private val entries = ArrayList<UsbSerialPort>()

    private lateinit var deviceAdapter: USBDeviceAdapter
    private lateinit var usbManager: UsbManager

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                messageRefresh -> {
                    refreshDeviceList()
                    this.sendEmptyMessageDelayed(messageRefresh, refreshTimeoutMillis)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ibSettingsBack.setOnClickListener {
            finish()
        }

        setupActivity()
    }

    private fun setupActivity() {
        rvSettingsDevices.layoutManager = LinearLayoutManager(this)

        deviceAdapter = USBDeviceAdapter(entries, getUSBDeviceOnClick())

        rvSettingsDevices.adapter = deviceAdapter

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun refreshDeviceList() {
        val deviceAsyncTask = DeviceAsyncTask(usbManager)

        deviceAsyncTask.onDeviceUpdate = object : OnDeviceUpdate {
            override fun update(result: List<UsbSerialPort>) {
                entries.clear()
                entries.addAll(result)
                deviceAdapter.notifyDataSetChanged()
            }
        }

        deviceAsyncTask.execute(null)
    }

    private fun getUSBDeviceOnClick(): USBDeviceAdapter.USBDeviceOnClick {
        return object : USBDeviceAdapter.USBDeviceOnClick {
            override fun onClickDevice(usbSerialPort: UsbSerialPort) {
                AppApplication.usbSerialPort = usbSerialPort
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.sendEmptyMessage(messageRefresh)
    }

    override fun onPause() {
        super.onPause()
        handler.removeMessages(messageRefresh)
    }

    companion object {
        fun newIntent(context: Context) : Intent = Intent(context, SettingsActivity::class.java)
    }

}