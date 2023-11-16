package com.afifpermana.donor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.afifpermana.donor.model.HomeResponse
import com.afifpermana.donor.model.SendTokenFCMToServerResponse
import com.afifpermana.donor.model.TotalNotifResponse
import com.afifpermana.donor.service.HomeAPI
import com.afifpermana.donor.service.NotifikasiAPI
import com.afifpermana.donor.service.SendTokenFCMAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var radioGroup: RadioGroup
    private lateinit var frameLayout: FrameLayout
    private lateinit var linearLayout: LinearLayout
    lateinit var sharedPref: SharedPrefLogin

    private lateinit var nama : TextView
    private lateinit var kodePendonor : TextView
    private lateinit var goldar : TextView
    private lateinit var beratBadan : TextView
    private lateinit var jadwalTerdekat : TextView
    private lateinit var fotoProfile : CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPrefLogin(this)

        if (sharedPref.getStatusLogin() == false){
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }else{
            FirebaseApp.initializeApp(this)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("token_fcm", "Fetching FCM token failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Dapatkan token FCM pengguna
                    val token = task.result
                    Log.e("token_fcm", token)
                    // Kirim token ke server (Laravel)
                }

//            database = Firebase.database.reference
//            writeNewUser("1","afif","afif@gmail.com")
            sendTokenToServer(sharedPref.getString("token_fcm").toString())
            homeView()
            setContentView(R.layout.activity_main)
            frameLayout = findViewById(R.id.frame_container)
            linearLayout = findViewById(R.id.home)
            linearLayout.visibility = View.VISIBLE

            nama = findViewById(R.id.nama)
            kodePendonor = findViewById(R.id.kode_donor)
            goldar = findViewById(R.id.text_goldar)
            beratBadan = findViewById(R.id.text_berat_badan)
            jadwalTerdekat = findViewById(R.id.text_jadwal_terdekat)
            fotoProfile = findViewById(R.id.foto_profile)

            totalNotif()

            radioGroup = findViewById(R.id.rg)
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.btn_artikel -> {
                        replaceFragmentHome(ArtikelFragment())
                    }
                    R.id.btn_faq -> {
                        replaceFragmentHome(FaqFragment())
                    }
//                    R.id.btn_call -> {
//                        replaceFragmentHome(CallFragment())
//                    }
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
                        homeView()
                        totalNotif()
                        frameLayout.visibility = View.GONE
                        linearLayout.visibility = View.VISIBLE
                        true
                    }
                    R.id.btn_location_donor -> {
                        replaceFragment(LocationFragment())
                        linearLayout.visibility = View.GONE
                        frameLayout.visibility = View.VISIBLE
                        totalNotif()
                        true
                    }R.id.btn_forum -> {
                        replaceFragment(DiskusiFragment())
                        linearLayout.visibility = View.GONE
                        frameLayout.visibility = View.VISIBLE
                        totalNotif()
                        true
                    }R.id.btn_riwayat -> {
                        replaceFragment(RiwayatDonorFragment())
                        linearLayout.visibility = View.GONE
                        frameLayout.visibility = View.VISIBLE
                        totalNotif()
                        true
                    }
                    R.id.btn_profile -> {
                        replaceFragment(ProfileFragment())
                        linearLayout.visibility = View.GONE
                        frameLayout.visibility = View.VISIBLE
                        bottomNavigationView.getOrCreateBadge(R.id.btn_profile).apply {
                            number = 0
                            isVisible = false
                            backgroundColor = resources.getColor(R.color.red)
                        }
                        true
                    }
                    else -> false
                }
            }
            replaceFragmentHome(ArtikelFragment())
        }
    }
    private fun totalNotif() {
        val retro = Retro().getRetroClientInstance().create(NotifikasiAPI::class.java)
        retro.totalNotif("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<TotalNotifResponse> {
            override fun onResponse(
                call: Call<TotalNotifResponse>,
                response: Response<TotalNotifResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    if (res?.total_notif != 0){
                        bottomNavigationView.getOrCreateBadge(R.id.btn_profile).apply {
                            number = res?.total_notif.toString().toInt()
                            isVisible = true
                            backgroundColor = resources.getColor(R.color.red)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TotalNotifResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("masalah", t.message.toString())
            }
        })
    }
    private fun sendTokenToServer(token: String) {
        Log.e("testing","sendTokenToServer")
        val retro = Retro().getRetroClientInstance().create(SendTokenFCMAPI::class.java)
        retro.senToken("Bearer ${sharedPref.getString("token")}",token).enqueue(object : Callback<SendTokenFCMToServerResponse> {
            override fun onResponse(
                call: Call<SendTokenFCMToServerResponse>,
                response: Response<SendTokenFCMToServerResponse>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
                        Log.e("success","success")
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<SendTokenFCMToServerResponse>, t: Throwable) {
                Log.e("error",t.message.toString())
            }
        })
    }

//    fun writeNewUser(userId: String, name: String, email: String) {
//        val user = User(name, email)
//
//        database.child("users").child(userId).setValue(user)
//    }

    private fun homeView() {
        val retro = Retro().getRetroClientInstance().create(HomeAPI::class.java)
            retro.home("Bearer ${sharedPref.getString("token")}").enqueue(object : Callback<HomeResponse> {
                override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                    val res = response.body()
                    val resCode = response.code()
                    Log.e("Status response" , response.code().toString())
                    Log.e("Status response" , sharedPref.getString("token").toString())
                    if (resCode == 200){
                        if (res != null){
                            sharedPref.setIdPendonor(res.user!!.id!!)
                            nama.text = res.user!!.nama
                            if(res.user!!.gambar.isNullOrEmpty()){
                                fotoProfile.setImageResource(R.drawable.baseline_person_24)
                            }else{
                                val path = "http://213.35.121.183/images/${res.user!!.gambar}"
                                Picasso.get().load(path).into(fotoProfile)
                                fotoProfile.setOnClickListener {
                                    showAlertGambar(path)
                                }
                            }
                            kodePendonor.text = res.user!!.kode_pendonor
                            goldar.text = res.user!!.id_golongan_darah.nama
                            beratBadan.text = "${res.user!!.berat_badan} KG"
                            if (res.user?.jadwal_terdekat != null){
                                val jadwalTerdekatDate = res.user?.jadwal_terdekat?.tanggal_donor
                                val waktu = res.user?.jadwal_terdekat?.jam_mulai
                                jadwalTerdekat.text = jadwalTerdekatDate

//                                // Cek apakah tanggal berubah
//                                val previousJadwal = sharedPref.getString("previous_jadwal")
//                                val previousWaktu = sharedPref.getString("previous_waktu")
//                                if (jadwalTerdekatDate != previousJadwal || waktu != previousWaktu) {
//                                    // Tanggal berubah, atur ulang status notifikasi
//                                    sharedPref.notification_set(false)
//                                }
//
//                                notif(jadwalTerdekatDate!!,waktu!!)
//
//                                // Simpan tanggal terbaru ke SharedPreferences
//                                sharedPref.setString("previous_jadwal", jadwalTerdekatDate!!)
//                                sharedPref.setString("previous_waktu", waktu!!)
                            }else{
                                jadwalTerdekat.text = "-"
                            }
                        }
                    }
                    else{
                        sharedPref.logOut()
                        sharedPref.setStatusLogin(false)
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }

                override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                    Log.e("Status response", t.message.toString())
                }
            })

    }

    private fun notif(tanggal: String, waktu: String) {
        val notifAlreadySet = sharedPref.getBoolean("notification_set")

        if (!notifAlreadySet) {
            // Tanggal dan waktu dalam format yang diberikan
            val inputDate = tanggal
            val inputTime = waktu
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = timeFormat.parse(inputTime)
            val formattedTime = timeFormat.format(time) // Mengonversi ke format "HH:mm"
            val title = "Jadwal Donor Darah"
            val content = "Jadwal donor kamu pukul $formattedTime cek segera"

            // Buat format SimpleDateFormat
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateTimeFormat.parse("$inputDate $inputTime")

            // Tentukan waktu notifikasi 1 jam sebelum tanggal dan waktu yang diberikan
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.HOUR_OF_DAY, -1)
                // Inisialisasi AlarmManager
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                // Menambahkan data tambahan ke Intent
                intent.putExtra("title", title)
                intent.putExtra("content", content)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // Set alarm untuk notifikasi 1 jam sebelum tanggal dan waktu
                alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)

                // Set status notifikasi sudah ditetapkan
                sharedPref.notification_set(true)
        }
    }

    private fun showAlertGambar(path:String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val customeView = LayoutInflater.from(this).inflate(R.layout.alert_gambar,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val image = customeView.findViewById<ImageView>(R.id.dialogImageView)
        image.setOnClickListener {
            dialog.dismiss()
        }
        Picasso.get().load(path).into(image)
        dialog.window?.setDimAmount(1f)
        dialog.show()
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    private fun replaceFragmentHome(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_btn_home, fragment).commit()
    }
}