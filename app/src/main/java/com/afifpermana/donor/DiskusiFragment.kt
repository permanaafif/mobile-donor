package com.afifpermana.donor

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.PostAdapter
import com.afifpermana.donor.model.AddPostResponse
import com.afifpermana.donor.model.AddPostTextRequest
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.service.PostAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DiskusiFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var add_post: FloatingActionButton
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Post> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin
    private lateinit var show_image : ImageView
    private lateinit var post : ImageView
//    private var lastPosition = 0
    private var selectedImageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diskusi, container, false)

        recyclerView = view.findViewById(R.id.rv_post)
        sw_layout = view.findViewById(R.id.swlayout)
        add_post = view.findViewById(R.id.add_post)

        // Set up your RecyclerView and other functionality here
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Mendapatkan posisi terakhir yang terlihat
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                lastPosition = lastVisibleItemPosition
//                sharedPref.setInt("scrollPosition",lastPosition)
//                Log.e("posisi", lastVisibleItemPosition.toString())
                // Mengubah visibilitas dan elevation FAB berdasarkan arah scroll
                if (dy > 0 && add_post.isShown) {
                    add_post.hide()
                } else if (dy < 0 && !add_post.isShown) {
                    add_post.show()
                }
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        sw_layout = view.findViewById(R.id.swlayout)

        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = view.findViewById(R.id.rv_post)
        add_post = view.findViewById(R.id.add_post)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        sharedPref = SharedPrefLogin(requireActivity())
        postView()
        adapter = PostAdapter(newData)
        recyclerView.adapter = adapter

        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                postView()
                sw_layout.isRefreshing = false
            }, 1000)
        }

        add_post.setOnClickListener {
            showCostumeAlertDialog()
        }
    }

    private fun showCostumeAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val customeView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_add_post,null)
        builder.setView(customeView)
        val dialog = builder.create()
        // Tambahkan ini untuk menghindari menutup dialog saat menekan di luar area dialog
        dialog.setCanceledOnTouchOutside(false)
        val close = customeView.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }

        show_image = customeView.findViewById(R.id.show_image)
        val img = customeView.findViewById<ImageView>(R.id.image)
        val text = customeView.findViewById<EditText>(R.id.pesan)
        val textHelper = customeView.findViewById<TextView>(R.id.helper)
        post = customeView.findViewById(R.id.send)
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey))
                }

                if (textLength >= 500){
                    textHelper.visibility = View.VISIBLE
                    textHelper.text = "Postingan hanya boleh 500 karakter"
                }else{
                    textHelper.visibility = View.GONE
                }
            }
        })

        img.setOnClickListener {
            getImage()
        }

        post.setOnClickListener {
            val textValue = text.text.toString()
            if (textValue.isNotBlank() || selectedImageUri != null) {
                addPost(textValue, dialog)
            }else{
                textHelper.visibility = View.VISIBLE
                textHelper.text = "Isi postingan kamu terlebih dahulu ..."
            }
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun addPost(text: String, dialog: AlertDialog) {
        try {
            val textValue = text
            val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)

            if (selectedImageUri != null) {
                val fileSize = getFileSize(selectedImageUri!!)
                val maxSizeInBytes = 1 * 1024 * 1024 // Ukuran maksimum 1 MB

                if (fileSize > maxSizeInBytes) {
                    // Ukuran gambar terlalu besar
                    Toast.makeText(requireActivity(), "Ukuran gambar terlalu besar, Max 1MB", Toast.LENGTH_SHORT).show()
                    return
                }

                // Lanjutkan dengan mengunggah gambar jika ukurannya sesuai
                val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(
                    selectedImageUri!!, "r", null
                ) ?: return
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                var file = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(selectedImageUri!!))
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                var gambar = UploadRequestBodyFragment(file, "image", requireActivity())

                retro.addPostWithImage(
                    "Bearer ${sharedPref.getString("token")}",
                    MultipartBody.Part.createFormData("gambar", file.name, gambar),
                    RequestBody.create(MediaType.parse("text/plain"), textValue)
                ).enqueue(object : Callback<AddPostResponse> {
                    override fun onResponse(
                        call: Call<AddPostResponse>,
                        response: Response<AddPostResponse>
                    ) {
                        Log.e("sampai", "gambarden2")
                        val res = response.body()
                        if (response.isSuccessful) {
                            if (res?.success == true) {
                                dialog.dismiss()
                                selectedImageUri = null
                                clearData()
                                postView()
                                Toast.makeText(requireActivity(), "Berhasil Upload", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireActivity(), "terjadi kesalahan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<AddPostResponse>, t: Throwable) {
                        Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("sampai", t.message.toString())
                    }
                })
            } else {
                // Jika selectedImageUri == null, kirim tanpa gambar
                retro.addPostWithoutImage(
                    "Bearer ${sharedPref.getString("token")}",
                    text
                ).enqueue(object : Callback<AddPostResponse> {
                    override fun onResponse(
                        call: Call<AddPostResponse>,
                        response: Response<AddPostResponse>
                    ) {
                        Log.e("sampai", "gambarden2")
                        val res = response.body()
                        Log.e("sampai", response.code().toString())
                        if (response.isSuccessful) {
                            if (res?.success == true) {
                                dialog.dismiss()
                                clearData()
                                postView()
                                Toast.makeText(requireActivity(), "Berhasil Upload", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireActivity(), "terjadi kesalahan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<AddPostResponse>, t: Throwable) {
                        Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("sampai", t.message.toString())
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("salahden", e.toString())
        }
    }


    private fun postView() {
        val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)
        retro.post("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<List<PostRespone>> {
            override fun onResponse(
                call: Call<List<PostRespone>>,
                response: Response<List<PostRespone>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                    for (i in res!!) {
                        val data = Post(
                            i.id.toString().toInt(),
                            i.gambar_profile.toString(),
                            i.nama.toString(),
                            i.updated_at.toString(),
                            i.text.toString(),
                            i.gambar.toString(),
                            i.jumlah_comment.toString().toInt()
                        )
                        newData.add(data)
                    }
                    adapter.notifyDataSetChanged()
                }
//                var sv = sharedPref.getInt("scrollPosition")
//                Log.e("sv",sv.toString())
//                if (sv > 3){
//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    layoutManager.scrollToPosition(sv)
//                }
            }

            override fun onFailure(call: Call<List<PostRespone>>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("masalah", t.message.toString())
            }
        })
    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }

    // Fungsi untuk mendapatkan ukuran file dari Uri
    private fun getFileSize(uri: Uri): Long {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                return it.getLong(sizeIndex)
            }
        }
        return 0
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            selectedImageUri = data?.data
            show_image.visibility = View.VISIBLE
            post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
            show_image.setImageURI(selectedImageUri)
        }
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name =""
        val returnCursor = this.query(fileUri,null,null,null,null)
        if (returnCursor != null){
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
//    override fun onResume() {
//        super.onResume()
//        Log.e("lifeclycle", "resume")
//        clearData()
//        postView()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        sharedPref.setInt("scrollPosition",0)
//    }
}
