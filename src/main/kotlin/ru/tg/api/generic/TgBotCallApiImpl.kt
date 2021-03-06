package ru.tg.api.generic

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.tg.api.poll.SendPollSerializerAdapter
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

internal class TgBotCallApiImpl(token: String) : TgBotCallApi {

    private val client = OkHttpClient.Builder().readTimeout(Duration.ofSeconds(50)).build()
    private val gson = GsonBuilder()
        .registerTypeAdapter(TgApiCall.SendPoll::class.java, SendPollSerializerAdapter())
        .create()
    private val baseUrl = "https://api.telegram.org/bot$token"

    companion object {
        private val log = Logger.getLogger(TgBotCallApiImpl::class.simpleName)
    }

    override suspend fun <T> callApi(apiCall: TgApiCall<T>): T {
        log.info("Calling bot API : ${apiCall.callName()}, with params: $apiCall")
        val request = prepareHttpRequest(apiCall)
        return execSuspend(request).parse(apiCall.responseType())
    }

    private fun <T> prepareHttpRequest(apiCall: TgApiCall<in T>): Request {
        return Request.Builder()
            .url("$baseUrl/${apiCall.callName().v}")
            .method(
                "POST",
                gson.toJson(apiCall).toRequestBody("application/json".toMediaType())
            )
            .build()
    }

    private suspend fun <T> Response.parse(clazz: Class<out T>): T {
        val body = this.body
        return if (body != null) {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                val bdy = String(body.bytes())
                println("Body is: $bdy")
                gson.fromJson(bdy, clazz)
            }
        } else throw RuntimeException("unexpected empty body")
    }

    private suspend fun execSuspend(request: Request): Response {
        val cb = Cb()
        client.newCall(request).enqueue(cb)
        val response = cb.await()
        log.info("received response: $response")
        return response
    }

    private class Cb : Callback {
        private val cf = CompletableFuture<Response>()
        override fun onFailure(call: Call, e: IOException) {
            cf.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            cf.complete(response)
        }

        suspend fun await(): Response = cf.await()
    }
}