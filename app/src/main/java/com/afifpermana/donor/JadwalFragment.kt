package com.afifpermana.donor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.model.Jadwal
import com.afifpermana.donor.model.LokasiDonorResponse
import com.afifpermana.donor.service.JadwalUserAPI
import com.afifpermana.donor.service.LokasiDonorAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JadwalFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var adapter : JadwalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var cl_jadwal : ConstraintLayout
    var newData : ArrayList<Jadwal> = ArrayList()
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
    var status: Array<Boolean> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jadwal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPrefLogin(requireActivity())
        jadwalView()
        sw_layout = view.findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_jadwal_donor)
        cl_jadwal = view.findViewById(R.id.cl_jadwal)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = JadwalAdapter(newData)
        recyclerView.adapter = adapter
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                jadwalView()
                sw_layout.isRefreshing = false
            }, 1000)
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
        status = emptyArray()
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
    private fun jadwalView() {
        Log.e("lokasinya2","${sharedPref.getInt("id")}")
        val retro = Retro().getRetroClientInstance().create(JadwalUserAPI::class.java)
        retro.jadwalDonor("Bearer ${sharedPref.getString("token")}",sharedPref.getInt("id")).enqueue(object : Callback<List<LokasiDonorResponse>> {
            override fun onResponse(
                call: Call<List<LokasiDonorResponse>>,
                response: Response<List<LokasiDonorResponse>>
            ) {
                if (response.isSuccessful) {
                    cl_jadwal.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    Log.e("lokasinya2","test")
                    val res = response.body()
                    Log.e("jadwal",res.toString())
                        for(i in res!!){
                            id += i.id!!
                            tanggal += i.tanggal_donor!!
                            jamMulai += extractJamMenit(i.jam_mulai!!)
                            jamSelesai += extractJamMenit(i.jam_selesai!!)
                            lokasi += i.lokasi!!
                            alamat += i.alamat!!
                            kontak += i.kontak!!
                            latitude += i.latitude!!
                            longitude += i.longitude!!
                            status += false
                        }
                        for (x in tanggal.indices){
                            val data = Jadwal(id[x],tanggal[x], jamMulai[x], jamSelesai[x], lokasi[x], alamat[x], kontak[x], latitude[x], longitude[x],status[x])
                            newData.add(data)
                        }
                        adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<LokasiDonorResponse>>, t: Throwable) {
                cl_jadwal.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE

            }
        })
    }
}