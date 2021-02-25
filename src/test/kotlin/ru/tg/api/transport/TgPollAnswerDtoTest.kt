package ru.tg.api.transport

import org.junit.jupiter.api.Test

internal class TgPollAnswerDtoTest {

    @Test
    fun assertDeSerializeWorks() {
        "\"poll_answer\":{\"poll_id\":\"123\",\"user\":{\"id\":115417017,\"is_bot\":false,\"first_name\":\"Elias\",\"language_code\":\"en\"},\"option_ids\":[1]}}]}"
    }

}