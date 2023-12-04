package com.afifpermana.donor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.afifpermana.donor.R
import com.airbnb.lottie.LottieAnimationView

class WalkThroughAdapter(val context: Context): PagerAdapter() {

    val imgArray: IntArray = intArrayOf(
        R.raw.slide1, R.raw.slide2, R.raw.slide3
    )
    val titleArray: Array<String> = arrayOf(
        "Selamat datang di aplikasi DARA",
        "Menemukan acara donor darah terdekat",
        "Terhubung untuk Membangun Jaringan Kebaikan"
    )
    val deskripsiArray: Array<String> = arrayOf(
        "Aplikasi yang dirancang untuk membantu proses donor darah menjadi lebih mudah dan nyaman bagi Anda.",
        "Anda dapat melihat jadwal acara donor darah, lokasi, dan informasi terkait lainnya.",
        "Membangun jaringan kebaikan dan saling terhubung dengan sesama pendonor darah. Lebih dari sekadar memberikan darah, mari bersama-sama membentuk komunitas yang solid, penuh dukungan, dan peduli terhadap kesehatan bersama."
    )

    override fun getCount(): Int {
        return imgArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view:View = LayoutInflater.from(context).inflate(R.layout.slide_walkthrough,container,false)
        val textTitle: TextView = view.findViewById(R.id.tv_title)
        val deskTitle: TextView = view.findViewById(R.id.tv_deskripsi)
        val img: LottieAnimationView = view.findViewById(R.id.la_slide)

        textTitle.text = titleArray[position]
        deskTitle.text = deskripsiArray[position]
        img.setAnimation(imgArray[position])
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view:View = `object` as View
        container.removeView(view)
    }
}