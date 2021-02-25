package ru.tg.api.inlined

import ru.tg.api.poll.TgQuizOptions

inline class BotName(val v: String)

inline class FirstName(val v: String)
inline class LastName(val v: String)

inline class ChatType(val v: String)
inline class TgChatId(val v: Int)

inline class Offset(private val v: Long) {
    operator fun plus(vPlus: Int) = Offset(vPlus + v)
}

inline class ApiCallName(val v: String)
inline class Answer(val v: Int)
inline class Limit(val v: Long)
inline class TimeOut(val v: Long)
inline class TgPollQuestion(val v: String)
inline class TgPollExplanation(val v: String)
inline class Title(val v: String)
inline class UserName(val v: String)
inline class PollId(val v: String)
inline class ObjectId(val v: String)
inline class EntityId(val v: String)

inline class Question(private val v: String){

    fun forPoll(options: () -> TgQuizOptions) : ru.tg.api.poll.TgQuizQuestion {
        return ru.tg.api.poll.TgQuizQuestion(v, options.invoke())
    }
}
