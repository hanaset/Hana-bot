package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


val scheduleViewResponse = """
** 현재 검색하신 일정에 대한 정보와 참가자 명단입니다. **

{schedule}
""".trimIndent()

val scheduleViewNotFoundResponse = """
** 현재 검색하신 Calendar ID는 잘못된 요청입니다. **
""".trimIndent()

@Service
class ScheduleViewCommandService(
        private val calendarService: CalendarService
) : Command {

    @PostConstruct
    fun init() {
        val command = "!참가인원확인"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()
        channel?.let {
            it.createMessage(getResponse(getWords(event))).block()
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return if (contents == null) {
            scheduleViewNotFoundResponse
        } else {
            scheduleViewResponse.replace("{schedule}", contents["schedule"].toString())
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        return try {
            val words = event.message.content.split(" ")
            val calendarId = words[1].toLong()
            val res = calendarService.getCalendar(calendarId = calendarId, guildId = event.guildId.get().asLong())
            mutableMapOf("schedule" to res)
        } catch (ex: Exception) {
            null
        }
    }
}