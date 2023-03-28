package com.asemlab.quakes.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Response

suspend fun <T> performRequest(request: suspend () -> Response<T>): T? {
    return coroutineScope {
        async {
            val body = request()
            if (body.isSuccessful)
                body.body()
            else throw Exception(body.errorBody().toString())
        }
    }.await()
}