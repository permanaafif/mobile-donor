package com.afifpermana.donor.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CheckWaktu {
    private lateinit var waktu:String
    private lateinit var tanggal:String
    private lateinit var jam:String

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWaktu(dateTimeString: String){
        val (date, time) = splitDateTime(dateTimeString)
        this.tanggal = date
        this.jam = time
    }

    fun getTanggal():String{
        return this.tanggal
    }

    fun getJam():String{
        return this.jam
    }

    fun getWaktu():String{
        return this.waktu
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime() {
        // Mendapatkan waktu saat ini
        val currentTime = LocalDateTime.now()

        // Format waktu sesuai kebutuhan (opsional)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedTime = currentTime.format(formatter)
        this.waktu = formattedTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun splitDateTime(dateTimeString: String): Pair<String, String> {
        // Parsing string menjadi objek LocalDateTime
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)

        // Mendapatkan tanggal dan waktu terpisah
        val date = localDateTime.toLocalDate().toString()
        val time = String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)

        return Pair(date, time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bandingTanggal(date1:String, date2:String):Boolean{
        val result = compareDates(date1, date2)

        return when (result) {
//            1 -> println("Tanggal $date1String lebih besar dari tanggal $date2String")
//            -1 -> println("Tanggal $date1String lebih kecil dari tanggal $date2String")
//            0 -> println("Tanggal $date1String sama dengan tanggal $date2String")
            1 -> false
            -1 -> false
            0 -> true
            else -> false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun compareDates(dateString1: String, dateString2: String): Int {
        val date1 = parseDate(dateString1)
        val date2 = parseDate(dateString2)

        return date1.compareTo(date2)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }
}