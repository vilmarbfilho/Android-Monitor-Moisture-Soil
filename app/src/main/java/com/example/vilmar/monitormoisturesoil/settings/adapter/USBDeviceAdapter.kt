package com.example.vilmar.monitormoisturesoil.settings.adapter

import android.hardware.usb.UsbDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.vilmar.monitormoisturesoil.R
import com.example.vilmar.monitormoisturesoil.inflate
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.HexDump
import kotlinx.android.synthetic.main.item_device.view.*

/**
 * Created by vilmar on 24/11/17.
 */
class USBDeviceAdapter(private val usbSerialPortList: ArrayList<UsbSerialPort>, private val listener: USBDeviceOnClick) : RecyclerView.Adapter<USBDeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_device))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(usbSerialPortList[position], listener)

    override fun getItemCount() = usbSerialPortList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(usbSerialPort: UsbSerialPort, listener: USBDeviceOnClick) = with(itemView){

            val driver: UsbSerialDriver = usbSerialPort.driver
            val device: UsbDevice = driver.device

            tvDeviceadapterDetail.text = "Vendor ${HexDump.toHexString(device.vendorId.toShort())} - Product ${HexDump.toHexString(device.productId.toShort())}"

            cvDeviceadapterContainer.setOnClickListener {
                listener.onClickDevice(usbSerialPort)
            }
        }
    }

    interface USBDeviceOnClick {
        fun onClickDevice(usbSerialPort: UsbSerialPort)
    }

}