package com.afifpermana.donor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.provider.ArtikelViewModel
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.util.Retro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.TimeZone


class ArtikelFragment() : Fragment() {

    private lateinit var adapter: ArtikelAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ArtikelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artikel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ArtikelViewModel::class.java)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_artikel)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        if (viewModel.newData == null) {
            beritaView()
        } else {
            // Gunakan data yang sudah ada dalam ViewModel
            adapter = ArtikelAdapter(viewModel.newData!!)
            recyclerView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.newData?.clear()
        beritaView()
        Log.e("isi ulang", "on resume")

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
        retro.allBerita().enqueue(object : Callback<List<BeritaResponse>> {
            override fun onResponse(
                call: Call<List<BeritaResponse>>,
                response: Response<List<BeritaResponse>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    viewModel.newData = ArrayList()
                    for (i in res!!) {
                        val data = Artikel(
                            i.gambar.toString(),
                            i.judul.toString(),
                            i.deskripsi.toString(),
                            formatTanggal(i.update_at.toString()),
                        )
                        viewModel.newData?.add(data)
                    }
                    adapter = ArtikelAdapter(viewModel.newData!!)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<BeritaResponse>>, t: Throwable) {
                Log.e("hasilnya", t.message.toString())
            }
        })
    }
}
