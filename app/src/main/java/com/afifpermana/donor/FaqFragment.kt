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
            "Bagaimana proses donor darah berlangsung?",
            "Apa yang membuat DARA menjadi solusi unik di dunia donor darah?",
            "Bagaimana memperoleh informasi terkini seputar donor darah di DARA?",
            "Mengapa penting bagi para donor darah untuk merasakan atmosfer komunitas?",
            "Bagaimana DARA memanfaatkan fitur Riwayat untuk personalisasi pengalaman donor darah?"
        )

        faqDesc = arrayOf(
            "Donor darah adalah tindakan sukarela di mana seseorang menyumbangkan sejumlah kecil darahnya untuk tujuan medis. Tujuannya adalah untuk menyediakan pasokan darah yang aman dan memadai untuk keperluan medis, termasuk transfusi darah dan pengobatan berbagai penyakit.",
            "Orang yang sehat, berusia di atas 17 tahun (usia dapat bervariasi sesuai dengan wilayah), memiliki berat badan yang memadai, dan memenuhi persyaratan kesehatan tertentu dapat menjadi donor darah.",
            "Donor darah mengunjungi pusat donor darah atau unit bergerak, kemudian mereka akan mengisi formulir pertanyaan kesehatan, dilakukan pemeriksaan kesehatan, dan darah diambil melalui jarum yang terhubung ke kantong darah steril.",
            "DARA menggabungkan kebutuhan donor darah dengan teknologi modern, menyajikan pengalaman donor yang interaktif, informatif, dan terkoneksi.",
            "Pengguna dapat menjelajahi informasi terkini di beranda DARA, termasuk FAQ, Jadwal, dan artikel terbaru seputar donor darah untuk pengalaman yang lebih menarik.",
            "DARA memahami bahwa memberikan darah adalah pengalaman pribadi, dan atmosfer komunitas membantu mendukung, menginspirasi, dan merayakan pencapaian bersama.",
            "Melalui fitur Riwayat, DARA mencatat setiap langkah perjalanan donor, termasuk lokasi dan waktu, menciptakan catatan pribadi yang bermakna untuk setiap pengguna."
        )


        for (i in faqTitle.indices){
            val data = Faq(faqTitle[i],faqDesc[i])
            newData.add(data)
        }
    }

}