package ru.tg.api.generic

import ru.tg.api.transport.TgResponseDto
import ru.tg.api.transport.TgUpdatesDto

interface TgBotApi {

    suspend fun sendPoll(dto: TgApiCall.SendPoll): TgResponseDto

    suspend fun getUpdates(dto: TgApiCall.GetUpdates) : TgUpdatesDto
}