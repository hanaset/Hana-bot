package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarApplyService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val scheduleRejectResponse = """
** 참가를 거절하셨습니다. **

⛈ {apply}
""".trimIndent()

val scheduleRejectFailedResponse = """
** 참가 거절에 실패하였습니다. Calender ID와 추가사항을 다시 한번 확인 부탁드립니다. **
    
"!참가거절 [Calendar ID] [추가사항]"
""".trimIndent()

@Service
class ScheduleRejectCommandService(
    private val calendarApplyService: CalendarApplyService
): Command {

    @PostConstruct
    fun init() {
        val command = "!참가거절"
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
            scheduleRejectFailedResponse
        } else {
            scheduleRejectResponse.replace("{apply}", contents["apply"].toString())
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        return try {
            val words = event.message.content.split(" ")
            val calendarId = words[1].toLong()
            val comment = words.subList(2, words.size).joinToString(" ")
            val user = event.message.author.get()

            val res = calendarApplyService.reject(calendarId = calendarId, userId = user.id.asLong(), username = user.username, comment = comment)
            mutableMapOf("apply" to res)
        }catch (ex: Exception) {
            null
        }

    }
}