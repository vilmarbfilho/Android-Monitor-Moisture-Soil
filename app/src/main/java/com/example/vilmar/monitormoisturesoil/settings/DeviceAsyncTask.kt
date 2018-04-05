package com.example.vilmar.monitormoisturesoil.settings

import android.hardware.usb.UsbManager
import android.os.AsyncTask
import android.os.SystemClock
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

/**
 * Created by vilmarbf on 28/12/17.
 */

class DeviceAsyncTask(private val mUsbManager: UsbManager) : AsyncTask<Void, Void, List<UsbSerialPort>>() {

    lateinit var onDeviceUpdate: OnDeviceUpdate

    override fun doInBackground(vararg params: Void): List<UsbSerialPort> {
        SystemClock.sleep(1000)

        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager)
        val result = ArrayList<UsbSerialPort>()

        drivers.forEach { driver ->
            val ports = driver.ports
            result.addAll(ports)
        }

        return result
    }

    override fun onPostExecute(result: List<UsbSerialPort>?) {
        onDeviceUpdate.update(result ?: listOf())

    }

    interface OnDeviceUpdate {
        fun update(result: List<UsbSerialPort>)
    }

}