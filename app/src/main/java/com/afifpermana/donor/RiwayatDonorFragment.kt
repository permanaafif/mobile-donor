package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.airbnb.lottie.LottieAnimationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatDonorFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var adapter : RiwayatDonorAdapter
    private lateinit var total_donor_darah : TextView
    private lateinit var cv_total_donor_darah : CardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cl_riwayat : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView
    var newData : ArrayList<RiwayatDonor.Riwayat> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin

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
        sw_layout = view.findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        val layoutManager = LinearLayoutManager(context)
        total_donor_darah = view.findViewById(R.id.tv_total_donor_darah)
        cv_total_donor_darah = view.findViewById(R.id.cv_total_donor_darah)
        recyclerView = view.findViewById(R.id.rv_riwayat_donor)
        cl_riwayat = view.findViewById(R.id.cl_riwayat)
        loadingLottie = view.findViewById(R.id.loading)
        nodataLottie = view.findViewById(R.id.no_data)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RiwayatDonorAdapter(newData)
        recyclerView.adapter = adapter

        riwayatDonor()

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
        cl_riwayat.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        loadingLottie.visibility = View.VISIBLE
        nodataLottie.visibility = View.GONE
        val connectivityChecker = ConnectivityChecker(requireActivity())
        if (connectivityChecker.isNetworkAvailable()){
            //koneksi aktif
            val retro = Retro().getRetroClientInstance().create(RiwayatDonorAPI::class.java)
            retro.riwayatDonor("Bearer ${sharedPref.getString("token")}").enqueue(object :
                Callback<RiwayatDonorResponse> {
                override fun onResponse(
                    call: Call<RiwayatDonorResponse>,
                    response: Response<RiwayatDonorResponse>
                ) {
                    val resCode = response.code()
                    if (resCode == 200){
                        val res = response.body()
                        Log.e("total_donor", res!!.total_donor_darah.toString())
                        if (res!!.total_donor_darah.toString() != "null"){
                            if(res.total_donor_darah.toString().toInt() <= 0){
                                cv_total_donor_darah.visibility = View.VISIBLE
                                total_donor_darah.text = "Total Donor Darah: ${res.total_donor_darah.toString()} kantong"
                            }
                        }else{
                            cv_total_donor_darah.visibility = View.GONE
                        }
                        if (res!!.riwayat!!.isNotEmpty()){
                            for (i in res!!.riwayat!!){
                                val riwayat = RiwayatDonor.Riwayat(
                                    i.tanggal_donor.toString(),
                                    i.lokasi_donor.toString(),
                                    i.jumlah_donor.toString().toInt()
                                )
                                newData.add(riwayat)
                            }
                            adapter.notifyDataSetChanged()
                            cl_riwayat.visibility = View.GONE
                            loadingLottie.visibility = View.GONE
                            nodataLottie.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }else{
                            cl_riwayat.visibility = View.VISIBLE
                            loadingLottie.visibility = View.GONE
                            nodataLottie.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }else{
                        sharedPref.logOut()
                        sharedPref.setStatusLogin(false)
                        startActivity(Intent(requireActivity(),LoginActivity::class.java))
                    }
                }

                override fun onFailure(call: Call<RiwayatDonorResponse>, t: Throwable) {
                    cl_riwayat.visibility = View.VISIBLE
                    loadingLottie.visibility = View.GONE
                    nodataLottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    sharedPref.logOut()
                    sharedPref.setStatusLogin(false)
                    startActivity(Intent(requireActivity(),LoginActivity::class.java))
                    Toast.makeText(requireActivity(),"Terjadi Kelasalah",Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            //koneksi tidak aktif
            connectivityChecker.showAlertDialogNoConnection()
            cl_riwayat.visibility = View.VISIBLE
            loadingLottie.visibility = View.GONE
            nodataLottie.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }

}