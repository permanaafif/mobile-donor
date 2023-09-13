package com.afifpermana.donor.util

import android.content.Context
import android.content.SharedPreferences

class SharedPrefLogin (context: Context){
    val login = "login"
    val myPref = "Main_pref"
    var sharedPreferences : SharedPreferences
    val editor: SharedPreferences.Editor
    init {
        sharedPreferences = context.getSharedPreferences(myPref,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setStatusLogin(status: Boolean){
        editor.putBoolean(login,status).apply()
    }
    fun getStatusLogin(): Boolean{
        return sharedPreferences.getBoolean(login,false)
    }

    fun setData(
        id: Int,
        nama: String,
        kodePendonor: String,
        goldar:String,
        beratBadan: Int,
        token: String){
        editor.putInt("id",id)
        editor.putString("nama", nama)
        editor.putString("kodePendonor", kodePendonor)
        editor.putString("goldar", goldar)
        editor.putInt("beratBadan", beratBadan)
        editor.putString("token", token)
        editor.apply()
    }

    fun getInt(key:String): Int{
        return sharedPreferences.getInt(key,0)
    }
    fun getString(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun logOut(){
        editor.clear()
        editor.apply()
    }
}