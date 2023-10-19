package com.afifpermana.donor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.ArtikelAdapter
import com.afifpermana.donor.adapter.PostAdapter
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.service.PostAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiskusiFragment : Fragment() {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Post> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diskusi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        sw_layout = view.findViewById(R.id.swlayout)
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = view.findViewById(R.id.rv_post)
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
            }

            override fun onFailure(call: Call<List<PostRespone>>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }


}