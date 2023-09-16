package com.afifpermana.donor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class ArtikelFragment() : Fragment() {

    private lateinit var adapter: ArtikelAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Artikel> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artikel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_artikel)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        sharedPref = SharedPrefLogin(requireActivity())
        beritaView()
        adapter = ArtikelAdapter(newData)
        recyclerView.adapter = adapter
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


    private fun beritaView() {
        val retro = Retro().getRetroClientInstance().create(BeritaAPI::class.java)
        retro.allBerita("Bearer ${sharedPref.getString("token")}").enqueue(object : Callback<List<BeritaResponse>> {
            override fun onResponse(
                call: Call<List<BeritaResponse>>,
                response: Response<List<BeritaResponse>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    for (i in res!!) {
                        val data = Artikel(
                            i.gambar.toString(),
                            i.judul.toString(),
                            i.deskripsi.toString(),
                            formatTanggal(i.update_at.toString()),
                        )
                        newData.add(data)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<BeritaResponse>>, t: Throwable) {
                Log.e("hasilnya", t.message.toString())
            }
        })
    }
}
