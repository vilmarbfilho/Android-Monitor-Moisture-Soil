package com.example.vilmar.monitormoisturesoil

import android.app.Application
import com.hoho.android.usbserial.driver.UsbSerialPort

/**
 * Created by vilmar on 27/11/17.
 */
class AppApplication : Application() {

    var usbSerialPort : UsbSerialPort? = null

    override fun onCreate() {
        super.onCreate()

        instance = this

    }

    companion object {
        lateinit var instance: AppApplication
        var usbSerialPort: UsbSerialPort? = null
    }

}