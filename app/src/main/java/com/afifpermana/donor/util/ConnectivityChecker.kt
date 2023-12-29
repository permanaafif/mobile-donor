package com.afifpermana.donor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.afifpermana.donor.R

class ConnectivityChecker(private val context: Context) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun showAlertDialogNoConnection() {
        val builder = AlertDialog.Builder(context)
        val customeView = LayoutInflater.from(context).inflate(R.layout.alert_network_connection,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val btnOke = customeView.findViewById<Button>(R.id.btn_oke)

        btnOke.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}
