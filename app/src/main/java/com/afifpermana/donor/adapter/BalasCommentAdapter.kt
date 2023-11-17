package com.afifpermana.donor.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.OtherDonorProfileActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.BalasCommentTo
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.service.CallBackData
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class BalasCommentAdapter(
    private var listBalasComment : List<BalasCommentTo>,
    private var dataCallBack: CallBackData,
    private var sharedPref: SharedPrefLogin
): RecyclerView.Adapter<BalasCommentAdapter.ViewHolder>() {

    var id_pendonor_now = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_balas_comment, parent, false)
    )

    override fun onBindViewHolder(holder: BalasCommentAdapter.ViewHolder, position: Int) {
        val balasComment = listBalasComment[position]
        var path_foto_profile = "http://213.35.121.183/images/${balasComment.gambar}"
        if (!balasComment.gambar.isNullOrEmpty() || balasComment.gambar != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto_profile)
            holder.foto_profile.setOnClickListener {
                showAlertGambar(holder.itemView.context,path_foto_profile)
            }
        }
        if (balasComment.gambar == "null" || balasComment.gambar.isNullOrEmpty()){
            holder.foto_profile.setImageResource(R.drawable.baseline_person_24)
        }

        holder.nama.text = balasComment.nama.capitalize()
        holder.nama.setOnClickListener {
            val id_pendonor = sharedPref.getInt("id")
            if (id_pendonor != balasComment.id_pendonor){
                if (id_pendonor_now != balasComment.id_pendonor){
                    val context = it.context
                    val i = Intent(context, OtherDonorProfileActivity::class.java)
                    i.putExtra("id_pendonor",balasComment.id_pendonor)
                    context.startActivity(i)
                }
            }
        }
        holder.tv_comment.text = balasComment.text
        holder.tgl_comment.text = balasComment.updated_at

        holder.btn_report.setOnClickListener {
            showCostumeAlertDialog(holder.itemView.context,balasComment.id)
        }
    }

    override fun getItemCount() = listBalasComment.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val foto_profile = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val tv_comment = view.findViewById<TextView>(R.id.tv_comment)
        val tgl_comment = view.findViewById<TextView>(R.id.tgl_comment)
        val btn_report = view.findViewById<ImageView>(R.id.btn_report)

    }

    private fun showCostumeAlertDialog(context: Context, id:Int) {
        val builder = AlertDialog.Builder(context)
        val customeView = LayoutInflater.from(context).inflate(R.layout.alert_report,null)
        builder.setView(customeView)
        val dialog = builder.create()
        // Tambahkan ini untuk menghindari menutup dialog saat menekan di luar area dialog
        dialog.setCanceledOnTouchOutside(false)
        val close = customeView.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }

        val text = customeView.findViewById<TextView>(R.id.pesan)
        val type = "Balasan"
        val textHelper = customeView.findViewById<TextView>(R.id.helper)
        val post = customeView.findViewById<ImageView>(R.id.send)
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
                }

                if (textLength >= 300){
                    textHelper.visibility = View.VISIBLE
                    textHelper.text = "laporan hanya boleh 300 karakter"
                }else{
                    textHelper.visibility = View.GONE
                }
            }
        })

        post.setOnClickListener {
            val textLength = text.text.toString().trim()
            if (textLength.isNotBlank()){
                val laporan = Laporan(null,null,id,textLength,type)
                dataCallBack.onAddLaporan(laporan)
                dialog.dismiss()
            }else{
                textHelper.text = "Tulis laporan ..."
                textHelper.visibility = View.VISIBLE
            }
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showAlertGambar(context: Context, path:String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val customeView = LayoutInflater.from(context).inflate(R.layout.alert_gambar,null)
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

    fun idPendonorNow(id: Int){
        id_pendonor_now = id
    }
}