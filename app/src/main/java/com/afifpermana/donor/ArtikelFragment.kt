package com.afifpermana.donor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.adapter.JadwalAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.Jadwal


class ArtikelFragment : Fragment() {

    private lateinit var adapter : ArtikelAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var newData : ArrayList<Artikel>

    lateinit var gambar : Array<Int>
    lateinit var judul : Array<String>
    lateinit var deskripsi : Array<String>
    lateinit var tanggal : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artikel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_artikel)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ArtikelAdapter(newData)
        recyclerView.adapter = adapter
    }

    private fun initView() {
        newData = arrayListOf<Artikel>()

        gambar = arrayOf(
            R.drawable.donor_darah,
            R.drawable.donor2,
            R.drawable.donor_darah,
            R.drawable.donor_darah
        )

        judul = arrayOf(
            "Krisi Donor Darah1",
            "Krisi Donor Darah2",
            "Krisi Donor Darah3",
            "Krisi Donor Darah"
        )

        deskripsi = arrayOf(
            "1Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incidid et dolore magna veniam, quis nostrud exercitat incidid et dolore magna veniam, quis nostrud exercitat",
            "2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incidid et dolore magna veniam, quis nostrud exercitat incidid et dolore magna veniam, quis nostrud exercitat",
            "3Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incidid et dolore magna veniam, quis nostrud exercitat incidid et dolore magna veniam, quis nostrud exercitat",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incidid et dolore magna veniam, quis nostrud exercitat incidid et dolore magna veniam, quis nostrud exercitat"
        )
        tanggal = arrayOf(
            "29/08/2023",
            "28/08/2023",
            "27/08/2023",
            "26/08/2023",
        )

        for (i in tanggal.indices){
            val data = Artikel(gambar[i],judul[i],deskripsi[i], tanggal[i])
            newData.add(data)
        }
    }
}