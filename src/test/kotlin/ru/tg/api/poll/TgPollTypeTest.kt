package ru.tg.api.poll

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class TgPollTypeTest {

    @Test
    fun testGetN() {
        Assertions.assertThat(TgPollType.QUIZ.n).isEqualTo("quiz")
        Assertions.assertThat(TgPollType.REGULAR.n).isEqualTo("regular")
    }
}