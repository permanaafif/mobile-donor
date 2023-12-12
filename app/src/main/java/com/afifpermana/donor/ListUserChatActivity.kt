package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.adapter.ListChatAdapter
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.UserChat
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListUserChatActivity : AppCompatActivity() {
    var userChatList = ArrayList<UserChat>()
    private lateinit var adapter: ListChatAdapter
    private lateinit var no_chat: CardView
    private lateinit var recyclerView : RecyclerView
    var newData : ArrayList<Comments> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user_chat)
        sharedPref = SharedPrefLogin(this)
        readUserChat(sharedPref.getInt("id").toString())
        no_chat = findViewById(R.id.no_chat)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rv_list_user_chat)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

    }

    private fun readUserChat(senderId: String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("ListUser").child(senderId)

        // Menggunakan Query untuk mengurutkan data berdasarkan waktu
//        val query = reference.orderByChild("time")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DataChange", "Data changed: ${snapshot.childrenCount} children found")
                userChatList.clear()

                if (snapshot.exists()) {
                    recyclerView.visibility = View.VISIBLE
                    no_chat.visibility = View.GONE
                    for (dataSnapShot: DataSnapshot in snapshot.children){
                        val chat = dataSnapShot.getValue(Chat::class.java)

                        if(chat!!.senderId.equals(senderId)){
                            profileUserChat(
                                chat!!.receiverId.toInt(),
                                chat!!.message,
                                chat!!.time,
                                true)
                        }else if (chat!!.receiverId.equals(senderId)){
                            profileUserChat(
                                chat!!.senderId.toInt(),
                                chat!!.message,
                                chat!!.time)
                        }
                    }
                }else{
                    recyclerView.visibility = View.GONE
                    no_chat.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Kesalahan",error.toString())
            }
        })
    }

    private fun profileUserChat(id:Int,pesan:String,waktu:String,kamu:Boolean = false) {
        val connectivityChecker = ConnectivityChecker(this)
        if (connectivityChecker.isNetworkAvailable()){
            //koneksi aktif
            val retro = Retro().getRetroClientInstance().create(ProfileAPI::class.java)
            retro.profileOtherDonor("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
                Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    val resCode = response.code()
                    if (resCode == 200){
                        var id:Int
                        var fotoProfile:String
                        var nama:String
                        Log.e("profilecaliak","success")
                        val res = response.body()!!

                        id = res.user.id!!
                        val pathFoto = "http://138.2.74.142/images/${res.user!!.gambar}"
                        if(!res.user.gambar.isNullOrEmpty()){
                            fotoProfile = pathFoto
                        }else{
                            fotoProfile = "null"
                        }
                        nama = res.user.nama.toString().capitalize()

                        val data = UserChat(id,kamu,fotoProfile,nama,pesan,waktu)
                        userChatList.add(data)
                        Log.e("userChatList",userChatList.toString())
                        adapter = ListChatAdapter(userChatList,this@ListUserChatActivity)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
//                    else{
//                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
//                    sharedPref.setStatusLogin(false)
//                    sharedPref.logOut()
//                    val intent = Intent(this@ListUserChatActivity, LoginActivity::class.java)
//                    startActivity(intent)
                    Toast.makeText(this@ListUserChatActivity,"Gagal", Toast.LENGTH_LONG).show()
                }
            })
        }else{
            //koneksi tidak aktif
            connectivityChecker.showAlertDialogNoConnection()
        }
    }
}