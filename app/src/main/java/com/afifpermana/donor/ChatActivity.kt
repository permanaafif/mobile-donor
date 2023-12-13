package com.afifpermana.donor

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ChatAdapter
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.model.NotificationData
import com.afifpermana.donor.model.NotificationPayload
import com.afifpermana.donor.model.UserTokenFCM
import com.afifpermana.donor.util.CheckWaktu
import com.afifpermana.donor.util.MainApplication
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call

class ChatActivity : AppCompatActivity() {

    private lateinit var nama:TextView
    private lateinit var foto_profile:CircleImageView
    private lateinit var message:EditText
    private lateinit var textHelper:TextView
    private lateinit var btnSendMessage:ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    var chatList = ArrayList<Chat>()
    var token = "null"



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

        var _nama = b!!.getString("nama")
        var path = b!!.getString("path")

        nama.text = _nama
        if (path != "null"){
            Picasso.get().load(path).into(foto_profile)
            foto_profile.setOnClickListener {
                showAlertGambar(path!!)
            }
        }

        message = findViewById(R.id.pesan)
        textHelper = findViewById(R.id.helper)
        btnSendMessage = findViewById(R.id.send)

        Log.e("datasaya",receiverId.toString())
        Log.e("datasaya",senderId.toString())

        var roomId = "RoomChat${receiverId.toString().toInt() + senderId.toString().toInt()}"

        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rv_chat)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ChatAdapter(this,senderId.toString(), chatList)
        recyclerView.adapter = adapter

        message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    btnSendMessage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ChatActivity, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    btnSendMessage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ChatActivity, R.color.grey))
                }

                if (textLength >= 300){
                    textHelper.visibility = View.VISIBLE
                    textHelper.text = "Pesan hanya boleh maksimal 300 karakter"
                }else{
                    textHelper.visibility = View.GONE
                }
            }
        })

        btnSendMessage.setOnClickListener {
            if (message.text.isNotEmpty()){
                Log.e("message","ok")
                sendMessage(senderId!!,receiverId!!,message.text.toString(),roomId)
            }
        }

        readMessage(senderId!!,receiverId!!,roomId)

    }

    // Fungsi untuk mengirim pesan ke Firebase
    private fun sendMessage(senderId: String, receiverId: String, message: String, roomId:String) {
        // Dapatkan referensi database
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("Message").child(roomId)

        // Buat child baru dengan ID yang dihasilkan otomatis
        val newChatRef = reference.push()

        // Buat HashMap dengan data yang ingin Anda kirim
        var time = CheckWaktu()
        time.getTime()
        Log.e("times",time.getWaktu())

        val messageMap = HashMap<String, String>()
        messageMap["senderId"] = senderId
        messageMap["receiverId"] = receiverId
        messageMap["message"] = message
        messageMap["time"] = time.getWaktu()

        // Kirim data ke Firebase Realtime Database
        newChatRef.setValue(messageMap)
            .addOnSuccessListener {
                Log.d("sendMessage", "Data berhasil dikirim!")
                this.message.text.clear()
                updateListUserMessage(senderId,receiverId,message,time.getWaktu())
                var _nama = b!!.getString("nama")
                var path = b!!.getString("path")

                readDataById(receiverId,_nama!!,path!!,message)
            }
            .addOnFailureListener { e ->
                Log.e("sendMessage", "Gagal mengirim data: $e")
            }
    }

    private fun updateListUserMessage(senderId: String, receiverId: String, message: String, time: String) {
        val sender: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("ListUser").child(senderId)
        val receiver: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("ListUser").child(receiverId)

        // Kombinasi senderId dan receiverId sebagai key unik
        val key = senderId.toInt()+receiverId.toInt()

        // Data chat yang ingin dimasukkan
        val userList = HashMap<String, String>()
        userList["senderId"] = senderId
        userList["receiverId"] = receiverId
        userList["message"] = message
        userList["time"] = time

        // Perbarui data pada sender
        sender.child(key.toString()).setValue(userList)
            .addOnSuccessListener {
                // Handle keberhasilan
                Log.d("updateListUserMessage", "Chat pada sender berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                // Handle kegagalan
                Log.e("updateListUserMessage", "Gagal memperbarui chat pada sender: $e")
            }

        // Perbarui data pada receiver
        receiver.child(key.toString()).setValue(userList)
            .addOnSuccessListener {
                // Handle keberhasilan
                Log.d("updateListUserMessage", "Chat pada receiver berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                // Handle kegagalan
                Log.e("updateListUserMessage", "Gagal memperbarui chat pada receiver: $e")
            }
    }
    private fun readMessage(senderId: String, receiverId: String, roomId:String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("Message").child(roomId)

        // Menggunakan Query untuk mengurutkan data berdasarkan waktu
//        val query = reference.orderByChild("time")

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

                // Pindahkan RecyclerView ke posisi terakhir setelah memperbarui data
                if (chatList.isNotEmpty()) {
                    recyclerView.smoothScrollToPosition(chatList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Kesalahan",error.toString())
            }
        })
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

    fun sendNotificationToDevice(deviceToken: String, title: String, body: String, data: Map<String, String>) {
        if (deviceToken != "null"){
            val notificationPayload = NotificationPayload(deviceToken, NotificationData(title, body, data))
            val call = (application as MainApplication).fcmService.sendNotification(notificationPayload)

            call.enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("Notification", "Notification sent. Response: ${response.code()}")
                    } else {
                        Log.e("Notification", "Failed to send notification. Response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Notification", "Failed to send notification. Error: ${t.message}")
                }
            })
        }
    }

    private fun readDataById(receiverId: String,nama: String,path: String,message: String) {
        // Dapatkan referensi database
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child("Users")

        // Gunakan addListenerForSingleValueEvent untuk mendapatkan data satu kali
        reference.child(receiverId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Data ditemukan, lakukan sesuatu dengan data
                    val user = snapshot.getValue(UserTokenFCM::class.java)
                    Log.d("ReadData", "Data ditemukan: $user")
                    val additionalData = mapOf("id_receiver" to receiverId, "nama" to nama,"path" to path)
                    Log.d("additionalData", "Data ditemukan: $additionalData")
                    sendNotificationToDevice(user!!.token_fcm,sharedPref.getString("nama")!!,message,additionalData)
                } else {
                    // Data tidak ditemukan
                    Log.d("ReadData", "Data tidak ditemukan untuk ID: $receiverId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("ReadData", "Gagal membaca data: $error")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("destroy","OK")
    }
}