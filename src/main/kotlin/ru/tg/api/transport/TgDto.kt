package ru.tg.api.transport

import com.google.gson.annotations.SerializedName
import ru.tg.api.inlined.*
import java.time.Duration

data class TgUpdatesDto(val ok: Boolean, val result: Collection<TgUpdateDto>) {
    fun lastUpdateId() = result.maxOf { it.updateId }
}

data class TgResponseDto(val ok: Boolean, val result: TgMessageDto)

data class TgUpdateDto(@SerializedName("update_id") val updateId: Long,
                       val message: TgMessageDto?,
                       @SerializedName("poll_answer") internal val pollAnswer: TgPollAnswerDto?)

data class TgMessageDto(@SerializedName("message_id") val msgId: Long,
                        val from: TgUserDto,
                        @SerializedName("sender_chat") val senderChat: TgChatDto,
                        val date: Long,
                        val chat: TgChatDto,
                        val text: String,
                        val poll: TgPollDto?)

data class TgUserDto(val id: Long,
                     @SerializedName("is_bot") val isBot: Boolean,
                     @SerializedName("first_name") val firstName: FirstName)

data class TgChatDto(
        val id: Long,
        val type: ChatType,
        val title: Title?,
        val username: UserName?,
        @SerializedName("first_name") val firstName: FirstName?,
        @SerializedName("last_name") val lastName: LastName?
)

data class TgPollDto(
        internal val id: PollId,
        internal val question: TgPollQuestion,
        internal val options: List<TgPollOptionDto>,
        @SerializedName("total_voter_count") internal val totalVoted: Int,
        @SerializedName("is_closed") internal val isClosed: Boolean,
        @SerializedName("is_anonymous") internal val isAnonymous: Boolean,
        internal val type: String,
        @SerializedName("allows_multiple_answers") internal val isMultipleAnswersAllowed: Boolean,
        internal val explanation: TgPollExplanation,
        @SerializedName("open_period") internal val openPeriod: Int,
        @SerializedName("close_date") internal val closeDate: Int
)

data class TgPollOptionDto(private val text: String,
                           @SerializedName("voter_count") private val votedCount: Int)

data class TgPollAnswerDto(@SerializedName("poll_id") internal val pollId: PollId,
                           private val user: TgUserDto,
                           @SerializedName("option_ids") private val selectedOptions: List<Int>)
