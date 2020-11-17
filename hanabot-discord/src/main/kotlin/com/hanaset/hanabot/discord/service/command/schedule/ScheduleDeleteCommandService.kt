package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val scheduleDeleteResponse = """
** 요청하신 아래의 일정이 삭제되었습니다. **

{schedule}
""".trimIndent()

val scheduleDeleteBadRequestResponse = """
** 일정삭제 대한 잘못된 요청입니다. **

[예시]
!일정삭제 [Calendar ID]
""".trimIndent()

@Service
class SchedulerDeleteCommandService(
    private val calendarService: CalendarService
): Command {

    @PostConstruct
    fun init() {
        val command = "!일정삭제"
        Commands.commands[command] = this
    }
    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()

        channel?.let {
            it.createMessage(getResponse(getWords(event))).block()
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {

        return if(contents != null) {
            scheduleDeleteResponse.replace("{schedule}", contents["schedule"].toString())
        } else {
            scheduleDeleteBadRequestResponse
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        return try {
            val words = event.message.content.split(" ")
            val res = calendarService.deleteCalender(words[1].toLong())

            mutableMapOf("schedule" to res)
        }catch (ex: Exception) {
            null
        }
    }
}