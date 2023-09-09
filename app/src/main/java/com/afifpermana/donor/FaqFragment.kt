package com.afifpermana.donor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.FaqAdapter
import com.afifpermana.donor.model.Faq

class FaqFragment : Fragment() {

    private lateinit var adapter : FaqAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var newData : ArrayList<Faq>

    lateinit var faqTitle : Array<String>
    lateinit var faqDesc : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rv_faq)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = FaqAdapter(newData)
        recyclerView.adapter = adapter
    }

    private fun initView() {
        newData = arrayListOf<Faq>()

        faqTitle = arrayOf(
            "Apa itu donor darah?",
            "Siapa yang memenuhi syarat untuk menjadi pendonor darah?",
            "Bagaimana proses donor darah berlangsung?"
        )

        faqDesc = arrayOf(
            "Donor darah adalah tindakan sukarela di mana seseorang menyumbangkan sejumlah kecil darahnya untuk tujuan medis. Tujuannya adalah untuk menyediakan pasokan darah yang aman dan memadai untuk keperluan medis, termasuk transfusi darah dan pengobatan berbagai penyakit.",
            "Orang yang sehat, berusia di atas 17 tahun (usia dapat bervariasi sesuai dengan wilayah), memiliki berat badan yang memadai, dan memenuhi persyaratan kesehatan tertentu dapat menjadi donor darah.",
            "Donor darah mengunjungi pusat donor darah atau unit bergerak, kemudian mereka akan mengisi formulir pertanyaan kesehatan, dilakukan pemeriksaan kesehatan, dan darah diambil melalui jarum yang terhubung ke kantong darah steril."
        )


        for (i in faqTitle.indices){
            val data = Faq(faqTitle[i],faqDesc[i])
            newData.add(data)
        }
    }

}