package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.afifpermana.donor.adapter.WalkThroughAdapter

class WalkThroughActivity : AppCompatActivity() {

    lateinit var wkAdapter: WalkThroughAdapter
    val dots = arrayOfNulls<TextView>(3)
    var currentPage: Int = 0

    lateinit var vp_wk : ViewPager
    lateinit var dotIndicator : LinearLayout
    lateinit var lewati : TextView
    lateinit var lanjutkan : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)

        wkAdapter = WalkThroughAdapter(this)
        vp_wk = findViewById(R.id.vp_walkthrough)
        dotIndicator = findViewById(R.id.ll_dots)
        lewati = findViewById(R.id.tv_lewati)
        lanjutkan = findViewById(R.id.tv_lanjutkan)
        vp_wk.adapter = wkAdapter

        initAction()
    }

    fun initAction(){
        vp_wk.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                dotIndicator(position)
                Log.e("posisi", position.toString())
                currentPage = position

                if (position == 2){
                    lanjutkan.text = "Masuk"
                    lanjutkan.setTextColor(resources.getColor(R.color.red))
                }else{
                    lanjutkan.text = "Lanjutkan"
                    lanjutkan.setTextColor(resources.getColor(R.color.background_donor))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        lanjutkan.setOnClickListener {
            if (vp_wk.currentItem + 1 < dots.size){
                vp_wk.currentItem += 1
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        lewati.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun dotIndicator(p:Int){
        dotIndicator.removeAllViews()

        for (i in 0..dots.size-1){
            dots[i] = TextView(this)
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.grey))
            dots[i]?.text = Html.fromHtml("&#8226;")

            dotIndicator.addView(dots[i])
        }

        if(dots.size > 0){
            dots[p]?.setTextColor(resources.getColor(R.color.red))
        }
    }
}