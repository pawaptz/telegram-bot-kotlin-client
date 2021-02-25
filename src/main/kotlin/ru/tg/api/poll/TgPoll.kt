package ru.tg.api.poll

import kotlinx.coroutines.channels.Channel
import ru.tg.api.inlined.PollId
import ru.tg.api.inlined.TgChatId
import ru.tg.api.inlined.TgPollExplanation
import ru.tg.api.inlined.TgPollQuestion
import ru.tg.api.transport.TgPollAnswerDto
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours

class TgPoll @ExperimentalTime constructor(
    val chatId: TgChatId,
    val question: TgPollQuestion,
    val options: TgQuizOptions,
    val tgPollType: TgPollType = TgPollType.REGULAR,
    val explanation: TgPollExplanation = TgPollExplanation(""),
    val openPeriod: Duration = 24.hours,
    val isAnonymous: Boolean = true
) {

    @Volatile
    internal var id: PollId = PollId("-1")

    private val updateChannel = Channel<TgPollAnswerDto>(100)

    sealed class TgQuizOption(val v: String) {
        class CorrectOption(correct: String) : TgQuizOption(correct)
        class IncorrectOption(incorrect: String) : TgQuizOption(incorrect)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TgQuizOption

            if (v != other.v) return false

            return true
        }

        override fun hashCode(): Int {
            return v.hashCode()
        }

        override fun toString(): String {
            return "${this.javaClass.name}(v='$v')"
        }
    }

    fun updateChannel(): Channel<TgPollAnswerDto> {
        return updateChannel
    }

    fun close() {
        updateChannel.close()
    }
}