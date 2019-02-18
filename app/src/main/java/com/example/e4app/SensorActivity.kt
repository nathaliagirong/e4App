package com.example.e4app

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog.show
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.empatica.empalink.ConnectionNotAllowedException
import com.empatica.empalink.EmpaDeviceManager
import com.empatica.empalink.EmpaticaDevice
import com.empatica.empalink.config.EmpaSensorType
import com.empatica.empalink.config.EmpaStatus
import com.empatica.empalink.delegate.EmpaDataDelegate
import com.empatica.empalink.delegate.EmpaStatusDelegate
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class SensorActivity : Activity(), EmpaDataDelegate, EmpaStatusDelegate{


    private val REQUEST_ENABLE_BT = 1

    private val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1


    private val EMPATICA_API_KEY = "72d372af2c044db1ae355227e19acc35" // TODO insert your API Key here


    private var deviceManager: EmpaDeviceManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        initEmpaticaDeviceManager()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_ACCESS_COARSE_LOCATION ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    initEmpaticaDeviceManager()
                } else {
                    // Permission denied, boo!
                    val needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry") { dialog, which ->
                                // try again
                                if (needRationale) {
                                    // the "never ask again" flash is not set, try again with permission request
                                    initEmpaticaDeviceManager()
                                } else {
                                    // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                            .setNegativeButton("Exit application") { dialog, which ->
                                // without permission exit is the only way
                                finish()
                            }
                            .show()
                }
        }
    }


    private fun initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_ACCESS_COARSE_LOCATION)
        } else {

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close") { dialog, which ->
                            // without permission exit is the only way
                            finish()
                        }
                        .show()
                return
            }

            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = EmpaDeviceManager(applicationContext, this, this)

            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager!!.authenticateWithAPIKey(EMPATICA_API_KEY)
        }
    }

    override fun onPause() {
        super.onPause()
        if (deviceManager != null) {
            deviceManager!!.stopScanning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (deviceManager != null) {
            deviceManager!!.cleanUp()
        }
    }






    override fun didReceiveTemperature(t: Float, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveTag(timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveGSR(gsr: Float, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveBatteryLevel(level: Float, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveAcceleration(x: Int, y: Int, z: Int, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveIBI(ibi: Float, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didReceiveBVP(bvp: Float, timestamp: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didUpdateSensorStatus(status: Int, type: EmpaSensorType?) {
        didUpdateOnWristStatus(status)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didUpdateOnWristStatus(status: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

    }

    override fun didUpdateStatus(status: EmpaStatus?) {
        // Update the UI
        statusLabel.text = status.toString()
        // updateLabel(statusLabel, status.name)

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            // updateLabel(statusLabel, status.name + " - Turn on your device")
            // Start scanning
            deviceManager!!.startScanning()
            // The device manager has established a connection

            // hide()

        } else if (status == EmpaStatus.CONNECTED) {

            // show()
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {

           //  updateLabel(deviceNameLabel, "")

           // hide()
        }

    }

    override fun didEstablishConnection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didDiscoverDevice(device: EmpaticaDevice?, deviceLabel: String?, rssi: Int, allowed: Boolean) {
        Log.i("allowed2", allowed.toString())
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager!!.stopScanning()
            try {
                // Connect to the device
                deviceManager!!.connectDevice(device)
                if (deviceLabel != null) {
                    toast(deviceLabel)
                }
                // Nombre del dispositivo
                // updateLabel(deviceNameLabel, "To: $deviceName")
            } catch (e: ConnectionNotAllowedException) {
                // This should happen only if you try to connect when allowed == false.
                toast("Sorry, you can't connect to this device")
            }

        }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}