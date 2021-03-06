package ru.tg.api.generic

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import ru.tg.api.poll.TgPoll
import ru.tg.api.transport.TgUpdateDto

interface TgBot {
    suspend fun sendPoll(poll: TgPoll): TgPoll

    @ExperimentalCoroutinesApi
    fun subscribe(): ReceiveChannel<TgUpdateDto>
}