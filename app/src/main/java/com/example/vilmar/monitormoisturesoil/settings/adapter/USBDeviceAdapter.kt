package com.example.vilmar.monitormoisturesoil.settings.adapter

import android.hardware.usb.UsbDevice
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.vilmar.monitormoisturesoil.R
import com.example.vilmar.monitormoisturesoil.inflate
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.HexDump

/**
 * Created by vilmar on 24/11/17.
 */
    class USBDeviceAdapter(private val usbSerialPortList: ArrayList<UsbSerialPort>, val listener: USBDeviceOnClick) : RecyclerView.Adapter<USBDeviceAdapter.DeviceViewHolder>() {

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) = holder.bind(usbSerialPortList[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder =
            DeviceViewHolder(parent.inflate(R.layout.item_device))

    override fun getItemCount(): Int = usbSerialPortList.size


    class DeviceViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var card : CardView = v.findViewById(R.id.cv_deviceadapter_container)
        var detail : TextView = v.findViewById(R.id.tv_deviceadapter_detail)

        fun bind(usbSerialPort: UsbSerialPort, listener: USBDeviceOnClick) {

            val driver: UsbSerialDriver = usbSerialPort.driver
            val device: UsbDevice = driver.device

            detail.text = "Vendor ${HexDump.toHexString(device.vendorId.toShort())} - Product ${HexDump.toHexString(device.productId.toShort())}"

            card.setOnClickListener {
                listener.onClickDevice(usbSerialPort)
            }
        }

    }

    interface USBDeviceOnClick {
        fun onClickDevice(usbSerialPort: UsbSerialPort)
    }

}