package com.example.vilmar.monitormoisturesoil.settings

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

    private val MESSAGE_REFRESH = 101
    private val REFRESH_TIMEOUT_MILLIS = 5000L

    private val mEntries = ArrayList<UsbSerialPort>()

    private lateinit var mDeviceAdapter: USBDeviceAdapter
    private lateinit var mUsbManager: UsbManager


    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_REFRESH -> {
                    refreshDeviceList()
                    this.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS)
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

        mDeviceAdapter = USBDeviceAdapter(mEntries, getUSBDeviceOnClick())

        rvSettingsDevices.adapter = mDeviceAdapter

        mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun refreshDeviceList() {
        val deviceAsyncTask = DeviceAsyncTask(mUsbManager)

        deviceAsyncTask.onDeviceUpdate = object : OnDeviceUpdate {
            override fun update(result: List<UsbSerialPort>) {
                mEntries.clear()
                mEntries.addAll(result)
                mDeviceAdapter.notifyDataSetChanged()
            }
        }

        deviceAsyncTask.execute(null)
    }

    private fun getUSBDeviceOnClick(): USBDeviceAdapter.USBDeviceOnClick {
        return object : USBDeviceAdapter.USBDeviceOnClick {
            override fun onClickDevice(usbSerialPort: UsbSerialPort) {
                AppApplication.sPort = usbSerialPort
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler.sendEmptyMessage(MESSAGE_REFRESH)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeMessages(MESSAGE_REFRESH)
    }

    companion object {
        fun newIntent(context: Context) : Intent = Intent(context, SettingsActivity::class.java)
    }

}