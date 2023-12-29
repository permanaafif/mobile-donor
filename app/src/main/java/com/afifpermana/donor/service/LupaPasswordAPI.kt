package com.afifpermana.donor.service

import com.afifpermana.donor.model.lupa_password.checkOtpRequest
import com.afifpermana.donor.model.lupa_password.checkOtpResponse
import com.afifpermana.donor.model.lupa_password.resetPasswordRequest
import com.afifpermana.donor.model.lupa_password.resetPasswordResponse
import com.afifpermana.donor.model.lupa_password.sendOtpRequest
import com.afifpermana.donor.model.lupa_password.sendOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LupaPasswordAPI {

    @POST("api/otp/send")
    fun sendOtp(@Body req: sendOtpRequest): Call<sendOtpResponse>

    @POST("api/otp/check")
    fun checkOtp(@Body req: checkOtpRequest): Call<checkOtpResponse>

    @POST("api/otp/reset-password")
    fun resetPassword(@Body req: resetPasswordRequest): Call<resetPasswordResponse>
}