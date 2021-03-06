package ru.tg.api.generic

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.tg.api.transport.TgUpdateDto
import ru.tg.api.transport.TgUpdatesDto
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class TgBotImplTest {

    private val callApi = mockk<TgBotCallApi>()
    private val bot = TgBotImpl(callApi, 0.toDuration(TimeUnit.SECONDS))

    @BeforeEach
    internal fun setUp() {
        coEvery { callApi.callApi(any<TgApiCall.GetUpdates>()) }
            .returns(TgUpdatesDto(result = listOf()))
    }

    @Test
    fun whenNewMessageThenSendItToAChannel() {
        val upd = mockk<TgUpdateDto>()
        every { upd.updateId }.returns(123)
        val channel = bot.subscribe()

        coEvery { callApi.callApi(any<TgApiCall.GetUpdates>()) }
            .returns(TgUpdatesDto(result = listOf(upd)))

        await.atMost(300, MILLISECONDS)
            .pollInterval(10, MILLISECONDS)
            .until {
                channel.poll() == upd
            }
    }
}