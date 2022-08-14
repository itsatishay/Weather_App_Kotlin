package com.kotlin.supweather

import okhttp3.*
import java.io.IOException

class ApiCall(
    private var url: String
) : Callback {
    private var onRequestCompleteListener : OnRequestCompleteListener? =null

    fun fetchJson(callback : OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)

    }

    override fun onFailure(call: Call, e: IOException) {
        onRequestCompleteListener?.onError(e.toString())
        println("error")
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            val body = response.body()?.string() ?: ""
            onRequestCompleteListener?.onSuccess(body)
        }
    }
}

interface OnRequestCompleteListener{
    fun onSuccess(response: String)
    fun onError(error: String)
}
