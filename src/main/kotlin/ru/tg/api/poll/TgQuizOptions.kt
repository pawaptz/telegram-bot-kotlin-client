package ru.tg.api.poll

import java.util.function.BiPredicate

data class TgQuizOptions(private val options: List<TgPoll.TgQuizOption>) {

    fun correctOptionId(): Int {
        return options.indexOfFirst { it is TgPoll.TgQuizOption.CorrectOption }.takeIf { it != -1 }
            ?: throw IllegalStateException("No correct answer found")
    }

    fun stringList(): List<String> = options.map { it.v }

    fun count(predicate: TgPoll.TgQuizOption.() -> Boolean) : Int {
        return options.filter(predicate).size
    }
}