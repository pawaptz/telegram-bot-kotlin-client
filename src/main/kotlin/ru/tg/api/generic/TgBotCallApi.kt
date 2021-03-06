package ru.tg.api.generic

internal interface TgBotCallApi {

    suspend fun <T> callApi(apiCall: TgApiCall<T>): T
}