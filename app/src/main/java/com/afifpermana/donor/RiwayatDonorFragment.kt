package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.adapter.RiwayatDonorAdapter
import com.afifpermana.donor.model.Jadwal
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.RiwayatDonor
import com.afifpermana.donor.model.RiwayatDonorResponse
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.service.RiwayatDonorAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatDonorFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var adapter : RiwayatDonorAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var cl_riwayat : ConstraintLayout
    var newData : ArrayList<RiwayatDonor> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin

    var tanggal_donor: Array<String> = arrayOf()
    var lokasi_donor: Array<String> = arrayOf()
    var jumlah_donor: Array<Int> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat_donor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPrefLogin(requireActivity())
        riwayatDonor()
        sw_layout = view.findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_riwayat_donor)
        cl_riwayat = view.findViewById(R.id.cl_riwayat)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RiwayatDonorAdapter(newData)
        recyclerView.adapter = adapter
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                riwayatDonor()
                sw_layout.isRefreshing = false
            }, 1000)
        }
    }

    private fun riwayatDonor() {
        val retro = Retro().getRetroClientInstance().create(RiwayatDonorAPI::class.java)
        retro.riwayatDonor("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<List<RiwayatDonorResponse>> {
            override fun onResponse(
                call: Call<List<RiwayatDonorResponse>>,
                response: Response<List<RiwayatDonorResponse>>
            ) {
                val resCode = response.code()
                if (resCode == 200){
                    cl_riwayat.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val res = response.body()!!
                    for (i in res){
                        tanggal_donor += i.tanggal_donor.toString()
                        lokasi_donor += i.lokasi_donor.toString()
                        jumlah_donor += i.jumlah_donor.toString().toInt()
                    }

                    for (x in tanggal_donor.indices){
                        val data = RiwayatDonor(tanggal_donor[x],lokasi_donor[x],jumlah_donor[x])
                        newData.add(data)
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    sharedPref.logOut()
                    sharedPref.setStatusLogin(false)
                    startActivity(Intent(requireActivity(),LoginActivity::class.java))
                }
            }

            override fun onFailure(call: Call<List<RiwayatDonorResponse>>, t: Throwable) {
                cl_riwayat.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun clearData() {
        newData.clear()
        tanggal_donor = emptyArray()
        lokasi_donor = emptyArray()
        jumlah_donor = emptyArray()
        adapter.notifyDataSetChanged()
    }

}