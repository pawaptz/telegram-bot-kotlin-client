package ru.tg.api.poll

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.tg.api.generic.TgApiCall
import java.lang.reflect.Type

class SendPollSerializerAdapter : JsonSerializer<TgApiCall.SendPoll> {

    override fun serialize(p0: TgApiCall.SendPoll, p1: Type, p2: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        val poll = p0.poll
        jsonObject.addProperty("question", poll.question.v)
        jsonObject.addProperty("chat_id", poll.chatId.v)
        jsonObject.addProperty("type", poll.tgPollType.n)
        jsonObject.addProperty("is_anonymous", poll.isAnonymous)
        jsonObject.addProperty("correct_option_id", poll.options.correctOptionId())
        jsonObject.add("options", p2.serialize(poll.options.stringList()))
        return jsonObject
    }
}