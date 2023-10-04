package com.afifpermana.donor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.model.Jadwal
import com.afifpermana.donor.model.LokasiDonorResponse
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.service.LokasiDonorAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class LocationFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var adapter: JadwalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var cl_jadwal : ConstraintLayout
    private var newData: MutableList<Jadwal> = mutableListOf()
    lateinit var sharedPref: SharedPrefLogin

    var id: Array<Int> = arrayOf()
    var tanggal: Array<String> = arrayOf()
    var jamMulai: Array<String> = arrayOf()
    var jamSelesai: Array<String>  = arrayOf()
     var lokasi: Array<String> = arrayOf()
     var alamat: Array<String> = arrayOf()
     var kontak: Array<String> = arrayOf()
     var latitude: Array<Double> = arrayOf()
     var longitude: Array<Double> = arrayOf()

    var lat : Double = 0.0
    var long : Double = 0.0

    private lateinit var locationManager: LocationManager
//    private lateinit var locationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPrefLogin(requireActivity())
        lokasiView()
        val layoutManager = LinearLayoutManager(context)
        sw_layout = view.findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = view.findViewById(R.id.rv_jadwal)
        cl_jadwal = view.findViewById(R.id.cl_jadwal)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = JadwalAdapter(newData)
        recyclerView.adapter = adapter

        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                lokasiView()
                sw_layout.isRefreshing = false
            }, 1000)
        }

        val dropdownJadwal: AutoCompleteTextView = view.findViewById(R.id.dropdown_filter_jadwal)
        val items = listOf("Tanggal", "Lokasi Terdekat")
        val adapterDropdown = ArrayAdapter(requireActivity(), R.layout.list_item, items)
        dropdownJadwal.setAdapter(adapterDropdown)

        dropdownJadwal.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
//            Toast.makeText(requireActivity(), "$itemSelected", Toast.LENGTH_LONG).show()

            when (itemSelected) {
                "Tanggal" -> {
                    // Urutkan data sumber berdasarkan tanggal terbesar/ terbaru
                    val sortedIndices = tanggal.indices.sortedByDescending { parseDate(tanggal[it]) }
                    // Mengisi newData dengan data yang sudah diurutkan
                    newData.clear()
                    for (i in sortedIndices) {
                        val data = Jadwal(id[i],tanggal[i], jamMulai[i], jamSelesai[i], lokasi[i], alamat[i], kontak[i], latitude[i], longitude[i])
                        newData.add(data)
                    }
                    // Memperbarui adapter dengan data yang sudah diurutkan
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireActivity(), "tanggal", Toast.LENGTH_LONG).show()
                }
                "Lokasi Terdekat" -> {
                    // Tambahkan logika untuk mengurutkan berdasarkan lokasi terdekat di sini
                    // Jika diperlukan
                    sortLocationsByNearestLocation(lat,long)
                    println("LatAfif: $lat, Long: $long")
                    Toast.makeText(requireActivity(), "Lokasi Terdekat", Toast.LENGTH_LONG).show()
                }
            }

        }

        // Inisialisasi LocationManager
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager


        // Cek izin lokasi
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Mendapatkan lokasi terbaru
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                lat = location.latitude
                long = location.longitude
                // Tampilkan lokasi dalam Toast
                Toast.makeText(
                    requireActivity(),
                    "Latitude: $latitude, Longitude: $longitude",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//               // Jika izin tidak diberikan, Anda dapat meminta izin di sini
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // Jika izin tidak diberikan, Anda dapat meminta izin di sini
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun clearData() {
        newData.clear()
        id = emptyArray()
        tanggal = emptyArray()
        jamMulai = emptyArray()
        jamSelesai = emptyArray()
        lokasi = emptyArray()
        alamat = emptyArray()
        kontak = emptyArray()
        latitude = emptyArray()
        longitude = emptyArray()
        adapter.notifyDataSetChanged()
    }
    fun extractJamMenit(waktu: String): String {
        val bagianWaktu = waktu.split(":")
        return if (bagianWaktu.size >= 2) {
            val jam = bagianWaktu[0]
            val menit = bagianWaktu[1]
            "$jam:$menit"
        } else {
            "Format waktu tidak valid"
        }
    }
    private fun lokasiView() {
        val retro = Retro().getRetroClientInstance().create(LokasiDonorAPI::class.java)
        retro.lokasi("Bearer ${sharedPref.getString("token")}").enqueue(object : Callback<List<LokasiDonorResponse>> {
            override fun onResponse(
                call: Call<List<LokasiDonorResponse>>,
                response: Response<List<LokasiDonorResponse>>
            ) {
                if (response.isSuccessful) {
                    cl_jadwal.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val res = response.body()

                    if (res != null) {
                        // Respons JSON tidak null

                        if (res.isNotEmpty()) {
                            // Respons JSON berisi elemen

                            for (i in res) {
                                id += i.id ?: 0
                                tanggal += i.tanggal_donor ?: ""
                                jamMulai += extractJamMenit(i.jam_mulai ?: "")
                                jamSelesai += extractJamMenit(i.jam_selesai ?: "")
                                lokasi += i.lokasi ?: ""
                                alamat += i.alamat ?: ""
                                kontak += i.kontak ?: ""
                                latitude += i.latitude ?: 0.0
                                longitude += i.longitude ?: 0.0
                            }

                            for (x in tanggal.indices) {
                                val data = Jadwal(
                                    id[x],
                                    tanggal[x],
                                    jamMulai[x],
                                    jamSelesai[x],
                                    lokasi[x],
                                    alamat[x],
                                    kontak[x],
                                    latitude[x],
                                    longitude[x]
                                )
                                newData.add(data)
                            }
                            adapter.notifyDataSetChanged()
                        } else {
                            // Respons JSON kosong
                            // Mungkin ada pesan atau tindakan yang perlu Anda lakukan di sini
                        }
                    } else {
                        // Respons JSON null
                        // Mungkin ada pesan atau tindakan yang perlu Anda lakukan di sini
                    }
                }

            }

            override fun onFailure(call: Call<List<LokasiDonorResponse>>, t: Throwable) {
                cl_jadwal.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }

        })
    }


    private fun sortLocationsByNearestLocation(userLatitude: Double, userLongitude: Double) {
        // Membuat daftar pasangan nilai jarak dan lokasi
        val distancesAndLocations = mutableListOf<Pair<Double, String>>()

        // Menghitung jarak untuk setiap lokasi dan menyimpannya dalam daftar pasangan
        for (i in 0 until latitude.size) {
            val locationLatitude = latitude[i]
            val locationLongitude = longitude[i]
            val distance = calculateDistance(userLatitude, userLongitude, locationLatitude, locationLongitude)
            distancesAndLocations.add(Pair(distance, lokasi[i]))
        }

        // Mengurutkan daftar berdasarkan jarak (ascending)
        distancesAndLocations.sortBy { it.first }

        // Mengambil lokasi yang sudah diurutkan
        val sortedLocations = distancesAndLocations.map { it.second }

        // Memperbarui adapter atau tampilan Anda dengan lokasi yang sudah diurutkan
        // Misalnya, Anda dapat menggunakan RecyclerView untuk menampilkan lokasi terurut
        // atau mengganti nilai dalam array "lokasi" dengan nilai yang sudah diurutkan.
        newData.clear()
        for (sortedLocation in sortedLocations) {
            val index = lokasi.indexOf(sortedLocation)
            if (index != -1) {
                val data = Jadwal(id[index],tanggal[index], jamMulai[index], jamSelesai[index], lokasi[index], alamat[index], kontak[index], latitude[index], longitude[index])
                newData.add(data)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val radius = 6371 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }

    private fun parseDate(date: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.parse(date)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}
