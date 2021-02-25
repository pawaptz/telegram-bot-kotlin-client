package ru.tg.api.generic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tg.api.inlined.Limit
import ru.tg.api.inlined.Offset
import ru.tg.api.inlined.PollId
import ru.tg.api.inlined.TimeOut
import ru.tg.api.poll.TgPoll
import ru.tg.api.transport.TgPollAnswerDto
import java.util.logging.Logger

@SuppressWarnings("unused")
@ExperimentalCoroutinesApi
class TgBotImpl(private val api: TgBotApi) : TgBot {

    @Volatile
    private var offset = Offset(0)

    private val polls = mutableMapOf<PollId, Channel<TgPollAnswerDto>>()
    private val mutex = Mutex()

    companion object {
        private val log = Logger.getLogger(TgBotImpl::class.simpleName)
    }

    init {
        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            while (isActive) {
                kotlin.runCatching { listenToUpdates() }
                delay(5_000)
                mutex.withLock {
                    polls.entries.removeAll { (_, v) -> v.isClosedForSend || v.isClosedForReceive }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun sendPoll(poll: TgPoll): TgPoll {
        val call: TgApiCall.SendPoll = TgApiCall.SendPoll(poll)
        val (ok, result) = api.sendPoll(call)
        if (ok && result.poll != null) {
            mutex.withLock {
                polls.putIfAbsent(result.poll.id, poll.updateChannel())
            }
        } else {
            throw RuntimeException("Failed to perform Telegram API call: $call, result: $ok, $result")
        }
        return poll
    }

    private suspend fun listenToUpdates() {
        val rez = api.getUpdates(
            TgApiCall.GetUpdates(offset, Limit(100), TimeOut(100))
        )
        if (rez.ok) {
            offset = Offset(rez.lastUpdateId()) + 1
            rez.result.forEach {
                val pollAnswer = it.pollAnswer
                if (pollAnswer != null) {
                    handlePollUpdate(pollAnswer)
                }
            }
        } else {
            log.severe("Error during polling updates: ${rez.result}")
        }
    }

    private suspend fun handlePollUpdate(pollDtoUpdate: TgPollAnswerDto) {
        mutex.withLock {
            with(polls[pollDtoUpdate.pollId]) {
                if (this != null) {
                    if (!this.offer(pollDtoUpdate))
                        log.warning("Failed to offer update to a channel: $pollDtoUpdate")
                } else {
                    log.warning("Failed to update channel, poll was not found ${pollDtoUpdate.pollId}")
                }
            }
        }
    }
}