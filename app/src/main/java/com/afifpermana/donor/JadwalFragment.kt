package com.afifpermana.donor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.model.Jadwal

class JadwalFragment : Fragment() {

    private lateinit var adapter : JadwalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var newData : ArrayList<Jadwal>

    lateinit var tanggal: Array<String>
    lateinit var jamMulai: Array<String>
    lateinit var jamSelesai: Array<String>
    lateinit var lokasi: Array<String>
    lateinit var alamat: Array<String>
    lateinit var kontak: Array<String>
    lateinit var latitude: Array<Double>
    lateinit var longitude: Array<Double>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jadwal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_jadwal_donor)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = JadwalAdapter(newData)
        recyclerView.adapter = adapter
    }

    private fun initView() {
        newData = arrayListOf<Jadwal>()

        tanggal = arrayOf(
            "29/08/2023",
            "26/08/2023"
        )

        jamMulai = arrayOf(
            "14:00",
            "14:00"
        )

        jamSelesai = arrayOf(
            "15:00",
            "15:00"
        )

        lokasi = arrayOf(
            "Bintaro Plaza",
            "Providence House"
        )

        kontak = arrayOf(
            "0888888888888888",
            "0666666666666666"
        )

        alamat = arrayOf(
            "Jl. Bintaro Utama 3A No.81, Pd. Karya, Kec. Pd. Aren, Kota Tangerang Selatan, Banten 15225",
            "MPVM+HP7, Jl. Masjid At-Tauhid, Kedaung, Kec. Pamulang, Kota Tangerang Selatan, Banten 15431"
        )

        latitude = arrayOf(
            -6.272617683478921,
            -6.305037074604995
        )

        longitude = arrayOf(
            106.74247972785645,
            106.734797881249
        )

        for (i in tanggal.indices) {
            val data = Jadwal(tanggal[i], jamMulai[i], jamSelesai[i], lokasi[i], alamat[i], kontak[i], latitude[i], longitude[i])
            newData.add(data)
        }
    }
}