package com.afifpermana.donor

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.afifpermana.donor.databinding.ActivityMapsBinding
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.model.DaftarJadwalDonorRequest
import com.afifpermana.donor.model.DaftarJadwalDonorResponse
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.service.JadwalUserAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var sharedPref: SharedPrefLogin

    val zoomLevel = 13f

    var b : Bundle? =null
    private lateinit var tanggal : TextView
    private lateinit var jam : TextView
    private lateinit var lokasi : TextView
    private lateinit var alamat : TextView
    private lateinit var kontak : TextView
    private lateinit var btnDaftar : Button
    private lateinit var btnClipBoard : ImageView
    private var id : Int = 0
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPref = SharedPrefLogin(this)

        tanggal = findViewById(R.id.tanggal)
        jam = findViewById(R.id.jam)
        lokasi = findViewById(R.id.lokasi)
        alamat = findViewById(R.id.alamat)
        kontak = findViewById(R.id.kontak)
        btnDaftar = findViewById(R.id.btn_daftar)
        btnClipBoard = findViewById(R.id.icon_copy)

        var id : Int
        b = intent.extras
        id = b!!.getInt("id")
        tanggal.text = b!!.getString("tanggal")
        jam.text = b!!.getString("jam")
        lokasi.text = b!!.getString("lokasi")
        alamat.text = b!!.getString("alamat")
        kontak.text = b!!.getString("kontak")
        latitude = b!!.getDouble("latitude")
        longitude = b!!.getDouble("longitude")

        checkDaftar(id)

        btnClipBoard.setOnClickListener {
            // Dapatkan instance ClipboardManager
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Salin teks ke clipboard
            val textToCopy = kontak.text
            val clip = ClipData.newPlainText("kontak",textToCopy)
            clipboard.setPrimaryClip(clip)

            // Beri tahu pengguna bahwa teks telah disalin
            Toast.makeText(this, "Kontak telah disalin ke clipboard", Toast.LENGTH_SHORT).show()

        }

        btnDaftar.setOnClickListener { showCostumeAlertDialog(id) }

    }

    private fun checkDaftar(x : Int) {
        val retro = Retro().getRetroClientInstance().create(JadwalUserAPI::class.java)
        retro.statusDaftar(sharedPref.getInt("id"),x).enqueue(object : Callback<DaftarJadwalDonorResponse> {
            override fun onResponse(
                call: Call<DaftarJadwalDonorResponse>,
                response: Response<DaftarJadwalDonorResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    Log.e("checkdaftar" , res!!.status.toString())
                    if (res != null){
                        if (res.status == true){
                            Log.e("checkdaftar" , "GONE")
                            btnDaftar.visibility = View.GONE
                        } else {
                            btnDaftar.visibility = View.VISIBLE // Hanya set terlihat jika status bukan true
                        }
                    } else {
                        btnDaftar.visibility = View.VISIBLE // Set terlihat jika respon null
                    }
                }
            }

            override fun onFailure(call: Call<DaftarJadwalDonorResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showCostumeAlertDialog(x: Int) {
        val builder = AlertDialog.Builder(this)
        val customeView = LayoutInflater.from(this).inflate(R.layout.alert,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val btnYes = customeView.findViewById<Button>(R.id.btn_yes)
        val btnNo = customeView.findViewById<Button>(R.id.btn_no)

        btnYes.setOnClickListener {
            Toast.makeText(this, "Kamu Telah Mendaftar", Toast.LENGTH_LONG).show()
            btnDaftar.visibility = View.GONE
            daftar(x)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun daftar(x: Int) {
        val daftar = DaftarJadwalDonorRequest()
        daftar.id_pendonor = sharedPref.getInt("id")
        daftar.id_jadwal_pendonor = x
        val retro = Retro().getRetroClientInstance().create(JadwalUserAPI::class.java)
        retro.daftar(daftar).enqueue(object : Callback<DaftarJadwalDonorResponse> {
            override fun onResponse(
                call: Call<DaftarJadwalDonorResponse>,
                response: Response<DaftarJadwalDonorResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    Log.e("checkdaftar" , res!!.status.toString())
                    if (res != null){
                        if (res.status == true){
                            Log.e("checkdaftar" , "GONE")
                            btnDaftar.visibility = View.GONE
                        } else {
                            btnDaftar.visibility = View.VISIBLE // Hanya set terlihat jika status bukan true
                        }
                    } else {
                        btnDaftar.visibility = View.VISIBLE // Set terlihat jika respon null
                    }
                }
            }

            override fun onFailure(call: Call<DaftarJadwalDonorResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val lokasiDonor = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(lokasiDonor).title("${lokasi.text}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiDonor, zoomLevel))

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))
            }
        }
    }
}