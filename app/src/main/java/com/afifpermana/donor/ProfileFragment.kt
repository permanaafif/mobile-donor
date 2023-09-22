package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.afifpermana.donor.model.PendonorLogoutResponse
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.service.PendonorLogoutAPI
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var edit : TextView
    private lateinit var logout : TextView
    private lateinit var sharedPref: SharedPrefLogin

    private lateinit var fotoProfile : CircleImageView
    private lateinit var nama : TextView
    private lateinit var email : TextView
    private lateinit var namaUser : TextView
    private lateinit var tanggal_lahir : TextView
    private lateinit var kode_pendonor : TextView
    private lateinit var alamat : TextView
    private lateinit var jenis_kelamin : TextView
    private lateinit var kontak : TextView
    private lateinit var goldar : TextView
    private lateinit var berat_badan : TextView
    var pathFoto : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPrefLogin(requireActivity())
        profileView()
        val layoutManager = LinearLayoutManager(context)
        edit = view.findViewById(R.id.btnEdit)
        logout = view.findViewById(R.id.btnLogOut)

        fotoProfile = view.findViewById(R.id.foto)
        nama = view.findViewById(R.id.nama)
        email = view.findViewById(R.id.email)
        namaUser = view.findViewById(R.id.namauser)
        tanggal_lahir = view.findViewById(R.id.tanggal_lahir)
        kode_pendonor = view.findViewById(R.id.kode)
        alamat = view.findViewById(R.id.alamat)
        jenis_kelamin = view.findViewById(R.id.jekel)
        kontak = view.findViewById(R.id.notelp)
        goldar = view.findViewById(R.id.goldar)
        berat_badan = view.findViewById(R.id.bb)

        edit.setOnClickListener{
            val i = Intent(context, ProfileEdit::class.java)
            i.putExtra("gambar", pathFoto.toString())
            i.putExtra("nama", nama.text)
            i.putExtra("email", email.text)
            i.putExtra("tanggal_lahir", tanggal_lahir.text)
            i.putExtra("alamat", alamat.text)
            i.putExtra("jenis_kelamin", jenis_kelamin.text)
            i.putExtra("kontak", kontak.text)
            i.putExtra("kode_pendonor", kode_pendonor.text)
            i.putExtra("berat_badan", berat_badan.text)
            startActivity(i)

        }

        logout.setOnClickListener{
            pendonorLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        profileView()
    }

    private fun profileView() {
        val retro = Retro().getRetroClientInstance().create(ProfileAPI::class.java)
        retro.profile("Bearer ${sharedPref.getString("token")}").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                val resCode = response.code()
                if (resCode == 200){
                    Log.e("profilecaliak","success")
                    val res = response.body()!!
                    if(res.user.gambar.isNullOrEmpty()){
                        fotoProfile.setImageResource(R.drawable.baseline_person_24)
                    }else{
                        Picasso.get().load("http://10.0.2.2:8000/images/${res.user!!.gambar}").into(fotoProfile)
                        pathFoto = "http://10.0.2.2:8000/images/${res.user!!.gambar}"
                    }
                    nama.text = res.user.nama
                    email.text = res.user.email
                    namaUser.text = res.user.nama
                    kode_pendonor.text = res.user.kode_pendonor
                    tanggal_lahir.text = res.user.tanggal_lahir
                    alamat.text = res.user.alamat_pendonor
                    jenis_kelamin.text = res.user.jenis_kelamin.toString().capitalize()
                    kontak.text = res.user.kontak_pendonor
                    goldar.text = res.user.id_golongan_darah.nama
                    berat_badan.text = "${res.user.berat_badan}"
                }
                else{
                    sharedPref.setStatusLogin(false)
                    sharedPref.logOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Gagal", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pendonorLogout() {
        val retro = Retro().getRetroClientInstance().create(PendonorLogoutAPI::class.java)
        retro.logout("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<PendonorLogoutResponse> {
            override fun onResponse(
                call: Call<PendonorLogoutResponse>,
                response: Response<PendonorLogoutResponse>
            ) {
                val res = response.code()
                if (res == 200){
                    sharedPref.setStatusLogin(false)
                    sharedPref.logOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(requireActivity(),"Gagal", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PendonorLogoutResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}