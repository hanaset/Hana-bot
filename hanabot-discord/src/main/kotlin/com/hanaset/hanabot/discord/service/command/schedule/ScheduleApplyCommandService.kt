package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarApplyService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val scheduleApplyResponse = """
** 참가를 신청하셨습니다. **

🏖 {apply}
""".trimIndent()

val scheduleApplyFailedResponse = """
** 참가 신청에 실패하였습니다. Calender ID와 추가사항을 다시 한번 확인 부탁드립니다. **
    
"!참가신청 [Calendar ID] [추가사항]"
""".trimIndent()

@Service
class ScheduleApplyCommandService(
    private val calendarApplyService: CalendarApplyService
): Command {

    @PostConstruct
    fun init() {
        val command = "!참가신청"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()

        channel?.let {
            it.createMessage(getResponse(getWords(event))).block()
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return if(contents == null) {
            scheduleApplyFailedResponse
        } else {
            scheduleApplyResponse.replace("{apply}", contents["apply"].toString())
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {
        return try {
            val words = event.message.content.split(" ")
            val calendarId = words[1].toLong()
            val user = event.message.author.get()
            val comment = words.subList(2, words.size).joinToString(" ")

            val res = calendarApplyService.apply(calendarId, user.id.asLong(), user.username, comment)

            mutableMapOf("apply" to res)
        }catch (ex: Exception) {
            null
        }
    }
}