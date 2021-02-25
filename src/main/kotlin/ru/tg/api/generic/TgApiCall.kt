package ru.tg.api.generic

import com.google.gson.Gson
import ru.tg.api.inlined.*
import ru.tg.api.poll.TgPoll
import ru.tg.api.transport.TgResponseDto
import ru.tg.api.transport.TgUpdatesDto

sealed class TgApiCall<T>(@Transient private val name: ApiCallName) {

    companion object {
        private val gson = Gson()
    }

    fun callName(): ApiCallName = name

    abstract fun responseType(): Class<T>

    abstract fun parseResponse(response: String): T

    data class SendPoll(
        val poll : TgPoll
    ) : TgApiCall<TgResponseDto>(ApiCallName("sendPoll")) {

        override fun responseType(): Class<TgResponseDto> = TgResponseDto::class.java

        override fun parseResponse(response: String): TgResponseDto {
            return gson.fromJson(response, TgResponseDto::class.java)
        }
    }

    data class GetUpdates(
        val offset: Offset = Offset(0),
        val limit: Limit = Limit(0),
        val timeout: TimeOut = TimeOut(100)
    ) : TgApiCall<TgUpdatesDto>(ApiCallName("getUpdates")) {

        override fun responseType(): Class<TgUpdatesDto> = TgUpdatesDto::class.java

        override fun parseResponse(response: String): TgUpdatesDto {
            return gson.fromJson(response, TgUpdatesDto::class.java)
        }
    }
}

