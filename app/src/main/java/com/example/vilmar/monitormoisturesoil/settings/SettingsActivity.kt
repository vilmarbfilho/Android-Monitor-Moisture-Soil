package com.example.vilmar.monitormoisturesoil.settings

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import com.example.vilmar.monitormoisturesoil.R
import com.example.vilmar.monitormoisturesoil.settings.adapter.USBDeviceAdapter
import com.hoho.android.usbserial.driver.UsbSerialPort
import android.text.method.TextKeyListener.clear
import android.widget.Toast
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialProber





/**
 * Created by vilmar on 24/11/17.
 */

class SettingsActivity : AppCompatActivity() {

    private val MESSAGE_REFRESH = 101
    private val REFRESH_TIMEOUT_MILLIS: Long = 5000


    private lateinit var mBackButton: ImageButton
    private lateinit var mRecyclerView: RecyclerView


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

        mBackButton = findViewById(R.id.ib_settings_back)
        mBackButton.setOnClickListener {
            finish()
        }

        mRecyclerView = findViewById(R.id.rv_settings_devices)

        init()
    }

    private fun init() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mDeviceAdapter = USBDeviceAdapter(mEntries)

        mRecyclerView.adapter = mDeviceAdapter

        mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun refreshDeviceList() {
        object : AsyncTask<Void, Void, List<UsbSerialPort>>() {
            override fun doInBackground(vararg params: Void): List<UsbSerialPort> {
                //toastIt("Refreshing device list ...")
                SystemClock.sleep(1000)

                val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager)

                val result = ArrayList<UsbSerialPort>()
                for (driver in drivers) {
                    val ports = driver.ports
                    //toastIt(String.format("+ %s: %s port%s",
                    //        driver, Integer.valueOf(ports.size), if (ports.size == 1) "" else "s"))

                    result.addAll(ports)
                }

                return result
            }

            override fun onPostExecute(result: List<UsbSerialPort>) {
                mEntries.clear()
                mEntries.addAll(result)
                mDeviceAdapter.notifyDataSetChanged()
            }

        }.execute(null as Void?)
    }

    private fun toastIt(msg: String) {
        runOnUiThread {
            Toast.makeText(SettingsActivity@this, msg, Toast.LENGTH_SHORT).show()
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