package ru.tg.api.generic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tg.api.inlined.Limit
import ru.tg.api.inlined.Offset
import ru.tg.api.inlined.PollId
import ru.tg.api.inlined.TimeOut
import ru.tg.api.poll.TgPoll
import ru.tg.api.transport.TgPollAnswerDto
import ru.tg.api.transport.TgUpdateDto
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@SuppressWarnings("unused")
@ExperimentalCoroutinesApi
class TgBotImpl @ExperimentalTime
internal constructor(
    private val callApi: TgBotCallApi,
    private val pollTimeOut: Duration = 5.toDuration(TimeUnit.SECONDS)
) : TgBot {

    companion object {
        private val log = Logger.getLogger(TgBotImpl::class.simpleName)

        @ExperimentalTime
        fun create(token: String): TgBot {
            return TgBotImpl(callApi = TgBotCallApiImpl(token))
        }
    }

    @Volatile
    private var offset = Offset(0)

    private val polls = mutableMapOf<PollId, Channel<TgPollAnswerDto>>()
    private val broadcastChannel = BroadcastChannel<TgUpdateDto>(BUFFERED)
    private val mutex = Mutex()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                kotlin.runCatching { listenToUpdates() }
                delay(pollTimeOut.toLongMilliseconds())
                mutex.withLock {
                    polls.entries.removeAll { (_, v) -> v.isClosedForSend || v.isClosedForReceive }
                }
            }
            broadcastChannel.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun sendPoll(poll: TgPoll): TgPoll {
        val call: TgApiCall.SendPoll = TgApiCall.SendPoll(poll)
        val (ok, result) = callApi.callApi(call)
        if (ok && result.poll != null) {
            mutex.withLock {
                polls.putIfAbsent(result.poll.id, poll.updateChannel())
            }
        } else {
            throw RuntimeException("Failed to perform Telegram API call: $call, result: $ok, $result")
        }
        return poll
    }

    @ExperimentalCoroutinesApi
    override fun subscribe(): ReceiveChannel<TgUpdateDto> = broadcastChannel.openSubscription()

    private suspend fun listenToUpdates() {
        val rez = callApi.callApi(
            TgApiCall.GetUpdates(offset, Limit(100), TimeOut(100))
        )
        if (rez.ok) {
            offset = Offset(rez.lastUpdateId()) + 1
            rez.result.forEach {
                broadcastChannel.offer(it)
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