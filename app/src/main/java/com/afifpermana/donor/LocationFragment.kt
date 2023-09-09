package com.afifpermana.donor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.model.Jadwal
import java.text.SimpleDateFormat
import java.util.Date

class LocationFragment : Fragment() {

    private lateinit var adapter: JadwalAdapter
    private lateinit var recyclerView: RecyclerView
    private var newData: MutableList<Jadwal> = mutableListOf()

    lateinit var tanggal: Array<String>
    lateinit var jamMulai: Array<String>
    lateinit var jamSelesai: Array<String>
    lateinit var lokasi: Array<String>
    lateinit var alamat: Array<String>
    lateinit var kontak: Array<String>
    lateinit var latitude: Array<Double>
    lateinit var longitude: Array<Double>

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
        initView()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_jadwal)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = JadwalAdapter(newData)
        recyclerView.adapter = adapter

        val dropdownJadwal: AutoCompleteTextView = view.findViewById(R.id.dropdown_filter_jadwal)
        val items = listOf("Tanggal", "Lokasi Terdekat")
        val adapterDropdown = ArrayAdapter(requireActivity(), R.layout.list_item, items)
        dropdownJadwal.setAdapter(adapterDropdown)

        dropdownJadwal.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
//            Toast.makeText(requireActivity(), "$itemSelected", Toast.LENGTH_LONG).show()

            when (itemSelected) {
                "Tanggal" -> {
                    // Urutkan data sumber berdasarkan tanggal terkecil
                    val sortedIndices = tanggal.indices.sortedBy { parseDate(tanggal[it]) }
                    // Mengisi newData dengan data yang sudah diurutkan
                    newData.clear()
                    for (i in sortedIndices) {
                        val data = Jadwal(tanggal[i], jamMulai[i], jamSelesai[i], lokasi[i], alamat[i], kontak[i], latitude[i], longitude[i])
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
                Toast.makeText(
                    requireActivity(),
                    "Lokasi tidak tersedia",
                    Toast.LENGTH_LONG
                ).show()
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

    private fun initView() {
        newData = arrayListOf<Jadwal>()
//        newData = mutableListOf()

        tanggal = arrayOf(
            "29/08/2023",
            "27/08/2023",
            "28/08/2023",
            "26/08/2023"
        )

        jamMulai = arrayOf(
            "14:00",
            "12:00",
            "14:00",
            "14:00"
        )

        jamSelesai = arrayOf(
            "15:00",
            "14:00",
            "15:00",
            "15:00"
        )

        lokasi = arrayOf(
            "Bintaro Plaza",
            "Taman Rempoa Indah",
            "SETU BUNGUR",
            "Providence House"
        )

        kontak = arrayOf(
            "0888888888888888",
            "0777777777777777",
            "0999999999999999",
            "0666666666666666"
        )

        alamat = arrayOf(
            "Jl. Bintaro Utama 3A No.81, Pd. Karya, Kec. Pd. Aren, Kota Tangerang Selatan, Banten 15225",
            "Jl. Palm Citra 3 Blok I No, Jl. Rempoa Raya No.2, RT.7/RW.2, Rempoa, Ciputat Timur, South Tangerang City, Banten 15412",
            "Jl. Menjangan III, Pd. Ranji, Kec. Ciputat Tim., Kota Tangerang Selatan, Banten 15412",
            "MPVM+HP7, Jl. Masjid At-Tauhid, Kedaung, Kec. Pamulang, Kota Tangerang Selatan, Banten 15431"
        )

        latitude = arrayOf(
            -6.272617683478921,
            -6.286140227917907,
            -6.289851400963546,
            -6.305037074604995
        )

        longitude = arrayOf(
            106.74247972785645,
            106.75724260631549,
            106.74024812993957,
            106.734797881249
        )

        for (i in tanggal.indices) {
            val data = Jadwal(tanggal[i], jamMulai[i], jamSelesai[i], lokasi[i], alamat[i], kontak[i], latitude[i], longitude[i])
            newData.add(data)
        }
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
                val data = Jadwal(tanggal[index], jamMulai[index], jamSelesai[index], lokasi[index], alamat[index], kontak[index], latitude[index], longitude[index])
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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.parse(date)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}
