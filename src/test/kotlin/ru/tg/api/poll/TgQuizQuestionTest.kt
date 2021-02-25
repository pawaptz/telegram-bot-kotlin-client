package ru.tg.api.poll

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class TgQuizQuestionTest {

    @Test
    fun assertQuizMustContainAtLeastOneOption() {
        assertThrows(IllegalStateException::class.java) {
            TgQuizQuestion("Question", TgQuizOptions(listOf()))
        }
    }

    @Test
    fun assertQuizMustContainAtLeastOneCorrectOption() {
        assertThrows(IllegalStateException::class.java) {
            TgQuizQuestion("Question", TgQuizOptions(listOf(TgPoll.TgQuizOption.IncorrectOption("inc"))))
        }
    }

    @Test
    fun assertQuizMustContainAtLeastOneIncorrectOption() {
        assertThrows(IllegalStateException::class.java) {
            TgQuizQuestion("Question", TgQuizOptions(listOf(TgPoll.TgQuizOption.CorrectOption("cor"))))
        }
    }

    @Test
    fun assertQuizWithTwoCorrectOptionsIsImpossible() {
        assertThrows(IllegalStateException::class.java) {
            TgQuizQuestion(
                "Question",
                TgQuizOptions(
                    listOf(
                        TgPoll.TgQuizOption.CorrectOption("cor1"),
                        TgPoll.TgQuizOption.CorrectOption("cor2")
                    )
                )
            )
        }
    }

    @Test
    fun assertQuizWithSingleCorrectOptionAndSingleInCorrectIsOk() {
        assertDoesNotThrow {
            TgQuizQuestion(
                "Question",
                TgQuizOptions(
                    listOf(
                        TgPoll.TgQuizOption.CorrectOption("cor"),
                        TgPoll.TgQuizOption.IncorrectOption("inc")
                    )
                )
            )
        }
    }

    @Test
    fun assertQuizWithSingleCorrectOptionAndMultipleInCorrectIsOk() {
        assertDoesNotThrow {
            TgQuizQuestion(
                "Question",
                TgQuizOptions(
                    listOf(
                        TgPoll.TgQuizOption.CorrectOption("cor"),
                        TgPoll.TgQuizOption.IncorrectOption("inc1"),
                        TgPoll.TgQuizOption.IncorrectOption("inc2")
                    )
                )
            )
        }
    }
}