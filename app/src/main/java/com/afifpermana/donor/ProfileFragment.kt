package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager

class ProfileFragment : Fragment() {

    private lateinit var edit : TextView
    private lateinit var logout : TextView

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

        edit.setOnClickListener{
            val intent = Intent(context, ProfileEdit::class.java)
            startActivity(intent)

        }

        logout.setOnClickListener{
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}