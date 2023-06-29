package com.asemlab.quakes.utils

import com.asemlab.quakes.remote.ServerResponse
import kotlinx.coroutines.coroutineScope
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
            throw e
        }
}