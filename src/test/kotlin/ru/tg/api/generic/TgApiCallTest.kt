package ru.tg.api.generic

import com.google.gson.Gson
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.tg.api.inlined.TgChatId
import ru.tg.api.inlined.TgPollQuestion
import ru.tg.api.poll.TgPoll
import ru.tg.api.poll.TgPoll.TgQuizOption.CorrectOption
import ru.tg.api.poll.TgPoll.TgQuizOption.IncorrectOption
import ru.tg.api.poll.TgQuizOptions
import ru.tg.api.transport.TgResponseDto
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class TgApiCallTest {

    private val gson = Gson()
    private val poll = TgPoll(
        chatId = TgChatId(1234), question = TgPollQuestion("Question"),
        options = TgQuizOptions(
            listOf(
                CorrectOption("1"),
                IncorrectOption("2")
            )
        )
    )

    @ParameterizedTest()
    @ValueSource(strings = ["updates\\poll\\update_json_poll_1.json"])
    fun assertSerializeDeserializeWorks(path: String) {
        val sendPollApiCall = TgApiCall.SendPoll(poll)
        testSource(path) {
            val (ok, result) = sendPollApiCall.parseResponse(it)

            assertThat(ok).isEqualTo(this.ok)
            assertThat(result).isEqualTo(this.result)
        }
    }

    private fun testSource(path: String, test: TgResponseDto.(String) -> Unit) {
        val rawResponse = String(Files.readAllBytes(Paths.get("src", "test", "resources", path)))
        val expected = gson.fromJson(rawResponse, TgResponseDto::class.java)
        test.invoke(expected, rawResponse)
    }
}