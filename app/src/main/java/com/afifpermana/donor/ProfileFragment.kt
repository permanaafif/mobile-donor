package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.afifpermana.donor.model.PendonorLogoutResponse
import com.afifpermana.donor.service.PendonorLogoutAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var edit : TextView
    private lateinit var logout : TextView
    private lateinit var sharedPref: SharedPrefLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        edit = view.findViewById(R.id.btnEdit)
        logout = view.findViewById(R.id.btnLogOut)

        sharedPref = SharedPrefLogin(requireActivity())
        edit.setOnClickListener{
            val intent = Intent(context, ProfileEdit::class.java)
            startActivity(intent)

        }

        logout.setOnClickListener{
            pendonorLogout()
        }
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