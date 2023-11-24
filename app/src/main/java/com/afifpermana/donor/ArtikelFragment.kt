package com.afifpermana.donor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.airbnb.lottie.LottieAnimationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class ArtikelFragment() : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var cl_artikel : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView
    private lateinit var loadmore : ProgressBar
    private lateinit var adapter: ArtikelAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Artikel> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin
    var isloading = false
    var page = 1
    var x = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_artikel, container, false)

        recyclerView = view.findViewById(R.id.rv_artikel)
        sw_layout = view.findViewById(R.id.swlayout)
        cl_artikel = view.findViewById(R.id.cl_artikel)
        loadingLottie = view.findViewById(R.id.loading)
        nodataLottie = view.findViewById(R.id.no_data)
        loadmore = view.findViewById(R.id.loadmore)

        // Set up your RecyclerView and other functionality here
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Mendapatkan posisi terakhir yang terlihat
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                lastPosition = lastVisibleItemPosition
//                sharedPref.setInt("scrollPosition",lastPosition)
//                Log.e("posisi", lastVisibleItemPosition.toString())
                // Mengubah visibilitas dan elevation FAB berdasarkan arah scroll

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if(dy > 0){
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (!isloading){
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount){
                            addMoreData()
//                            Log.e("RecyclerView","RecyclerView")
                        }
                    }
                }
            }
        })

        return view
    }

    private fun addMoreData() {
        isloading = true
        loadmore.visibility = View.VISIBLE

        Handler().postDelayed({
            isloading = false
            loadmore.visibility = View.GONE
            beritaView(page)
        },3000)
        page++
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        sw_layout = view.findViewById(R.id.swlayout)
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = view.findViewById(R.id.rv_artikel)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        sharedPref = SharedPrefLogin(requireActivity())
        cl_artikel = view.findViewById(R.id.cl_artikel)
        loadingLottie = view.findViewById(R.id.loading)
        nodataLottie = view.findViewById(R.id.no_data)
        loadmore = view.findViewById(R.id.loadmore)
        page = 1
        cl_artikel.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        loadingLottie.visibility = View.VISIBLE
        beritaView(page)
        adapter = ArtikelAdapter(newData)
        recyclerView.adapter = adapter

        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                page=1
                cl_artikel.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                loadingLottie.visibility = View.VISIBLE
                beritaView(page)
                sw_layout.isRefreshing = false
            }, 1000)
        }
    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }

    fun formatTanggal(waktu: String): String {
        try {
            val parts = waktu.split("T")
            if (parts.size >= 1) {
                val tanggalPart = parts[0]
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val tanggalDate = dateFormat.parse(tanggalPart)
                return dateFormat.format(tanggalDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Format waktu tidak valid"
    }

    private fun beritaView(page:Int) {
        val retro = Retro().getRetroClientInstance().create(BeritaAPI::class.java)
        retro.allBerita("Bearer ${sharedPref.getString("token")}",page).enqueue(object : Callback<List<BeritaResponse>> {
            override fun onResponse(
                call: Call<List<BeritaResponse>>,
                response: Response<List<BeritaResponse>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res.isNullOrEmpty()){
                        cl_artikel.visibility = View.VISIBLE
                        loadingLottie.visibility = View.GONE
                        nodataLottie.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }else{
                        x = true
                        // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                        val sortedData = res?.sortedByDescending { it.update_at }
                        for (i in sortedData!!) {
                            val data = Artikel(
                                i.gambar.toString(),
                                i.judul.toString(),
                                i.deskripsi.toString(),
                                formatTanggal(i.update_at.toString()),
                            )
                            newData.add(data)
                        }
                        adapter.notifyDataSetChanged()
                        cl_artikel.visibility = View.GONE
                        loadingLottie.visibility = View.VISIBLE
                        nodataLottie.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
                if(response.code() == 403 && x == false){
                    cl_artikel.visibility = View.VISIBLE
                    loadingLottie.visibility = View.GONE
                    nodataLottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                if(response.code() == 403 && x == true){
                    Toast.makeText(requireActivity(), "Anda telah mencapai akhir postingan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BeritaResponse>>, t: Throwable) {
                Log.e("hasilnya", t.message.toString())
            }
        })
    }

}
