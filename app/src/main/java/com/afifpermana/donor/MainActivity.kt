package com.afifpermana.donor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var radioGroup: RadioGroup
    private lateinit var frameLayout: FrameLayout
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        frameLayout = findViewById(R.id.frame_container)
        linearLayout = findViewById(R.id.home)
        linearLayout.visibility = View.VISIBLE

        radioGroup = findViewById(R.id.rg)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_artikel -> {
                    replaceFragmentHome(ArtikelFragment())
                }
                R.id.btn_faq -> {
                    replaceFragmentHome(FaqFragment())
                }
                R.id.btn_call -> {
                    replaceFragmentHome(CallFragment())
                }
                R.id.btn_jadwal -> {
                    replaceFragmentHome(JadwalFragment())
                }
            }
        }

        bottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.btn_home -> {
                    radioGroup.check(R.id.btn_artikel)
                    frameLayout.visibility = View.GONE
                    linearLayout.visibility = View.VISIBLE
                    true
                }
                R.id.btn_location_donor -> {
                    replaceFragment(LocationFragment())
                    linearLayout.visibility = View.GONE
                    frameLayout.visibility = View.VISIBLE
                    true
                }
                R.id.btn_profile -> {
                    replaceFragment(ProfileFragment())
                    linearLayout.visibility = View.GONE
                    frameLayout.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
        replaceFragmentHome(ArtikelFragment())
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    private fun replaceFragmentHome(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_btn_home, fragment).commit()
    }
}