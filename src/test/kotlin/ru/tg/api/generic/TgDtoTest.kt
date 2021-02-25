package ru.tg.api.generic

import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.tg.api.inlined.ChatType
import ru.tg.api.inlined.FirstName
import ru.tg.api.transport.TgChatDto
import ru.tg.api.transport.TgMessageDto
import ru.tg.api.transport.TgUserDto

class TgDtoTest {

    private val gson = Gson()

    @Test
    fun assertDeserializeWorks() {
        val input = "{\"id\":115417017,\"is_bot\":false,\"first_name\":\"Elias\",\"language_code\":\"en\"}"

        val (fromId, isBot, firstName) = gson.fromJson(input, TgUserDto::class.java)

        assertThat(fromId).isEqualTo(115417017)
        assertThat(isBot).isFalse
        assertThat(firstName).isEqualTo(FirstName("Elias"))
    }

    @Test
    fun assertChatDeserializeWorks() {
        val input = "{\"id\":115417017,\"first_name\":\"Elias\",\"type\":\"private\"}"

        val chat = gson.fromJson(input, TgChatDto::class.java)

        assertThat(chat.id).isEqualTo(115417017)
        assertThat(chat.firstName).isEqualTo(FirstName("Elias"))
        assertThat(chat.type).isNotNull
        assertThat(chat.type).isEqualTo(ChatType("private"))
        assertThat(chat.title).isNull()
    }

    @Test
    fun assertMessageDeserializeWorks() {
        val input = "{\"message_id\":3,\"from\":{\"id\":115417017,\"is_bot\":false,\"first_name\":\"Elias\",\"language_code\":\"en\"},\"chat\":{\"id\":115417017,\"first_name\":\"Elias\",\"type\":\"private\"},\"date\":1608662340,\"text\":\"abc\"}"
        val (msgId, from) = gson.fromJson(input, TgMessageDto::class.java)

        val fromExpected = TgUserDto(115417017, false, FirstName("Elias"))

        assertThat(msgId).isEqualTo(3)
        assertThat(from).isEqualTo(fromExpected)
    }
}