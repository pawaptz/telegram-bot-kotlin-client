package ru.tg.api.generic

import ru.tg.api.poll.TgPoll

interface TgBot {
    suspend fun sendPoll(poll: TgPoll) : TgPoll
}