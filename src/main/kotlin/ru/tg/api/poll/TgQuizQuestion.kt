package ru.tg.api.poll

data class TgQuizQuestion(
    val questionString: String,
    val quizOptions: TgQuizOptions
) {
    init {
        check(quizOptions.count { this is TgPoll.TgQuizOption.CorrectOption } == 1)
        check(quizOptions.count { this is TgPoll.TgQuizOption.IncorrectOption } >= 1)
    }
}