package com.afifpermana.donor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ChatAdapter
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {

    private lateinit var nama:TextView
    private lateinit var foto_profile:CircleImageView
    private lateinit var message:EditText
    private lateinit var btnSendMessage:ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    var chatList = ArrayList<Chat>()


    lateinit var sharedPref: SharedPrefLogin
    var b : Bundle? =null
    var receiverId : String? = null
    var senderId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        // Inisialisasi Firebase
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        sharedPref = SharedPrefLogin(this)
        b = intent.extras
        receiverId = b!!.getString("id_receiver")
        senderId = sharedPref.getInt("id").toString()

        nama = findViewById(R.id.nama)
        foto_profile = findViewById(R.id.foto)

        message = findViewById(R.id.pesan)
        btnSendMessage = findViewById(R.id.send)

        Log.e("datasaya",receiverId.toString())
        Log.e("datasaya",senderId.toString())

        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rv_chat)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ChatAdapter(this,senderId.toString(), chatList)
        recyclerView.adapter = adapter

        btnSendMessage.setOnClickListener {
            if (message.text.isNotEmpty()){
                Log.e("message","ok")
                sendMessage(senderId!!,receiverId!!,message.text.toString())
            }
        }

        readMessage(senderId!!,receiverId!!)

    }

    // Fungsi untuk mengirim pesan ke Firebase
    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        // Dapatkan referensi database
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        // Buat HashMap dengan data yang ingin Anda kirim
        val messageMap = HashMap<String, String>()
        messageMap["senderId"] = senderId
        messageMap["receiverId"] = receiverId
        messageMap["message"] = message

        // Kirim data ke Firebase Realtime Database
        reference.child("Chats").push().setValue(messageMap)
            .addOnSuccessListener {
                Log.d("sendMessage", "Data berhasil dikirim!")
                this.message.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("sendMessage", "Gagal mengirim data: $e")
            }
    }

    private fun readMessage(senderId: String, receiverId: String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DataChange", "Data changed: ${snapshot.childrenCount} children found")
                chatList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children){
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) &&  chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) &&  chat!!.receiverId.equals(senderId)){
                        chatList.add(chat)
                    }
                }
//                adapter = ChatAdapter(this@ChatActivity,this@ChatActivity.senderId.toString(),chatList)
//                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Kesalahan",error.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("destroy","OK")
    }
}