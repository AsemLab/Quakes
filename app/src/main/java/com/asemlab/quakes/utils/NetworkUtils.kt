package com.asemlab.quakes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.asemlab.quakes.remote.ServerResponse
import retrofit2.Response

suspend fun <T> performRequest(request: suspend () -> Response<T>): ServerResponse<T> {
    return try {
        val body = request()
        if (body.isSuccessful)
            ServerResponse.Success(body.body()!!)
        else
            ServerResponse.Error(
                body.errorBody()?.string() ?: "Empty error message",
                body.code()
            )
    } catch (e: Exception) {
        Log.e("performRequest", "${e.message}")
        throw e
    }
}

fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
}