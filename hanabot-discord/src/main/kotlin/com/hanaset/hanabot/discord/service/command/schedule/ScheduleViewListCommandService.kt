package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val scheduleViewListResponse = """
** 현재 등록 하신 일정은 아래와 같습니다. **

{schedule}
""".trimIndent()

val scheduleViewListNotFoundResponse = """
** 현재 등록 하신 일정이 없습니다. **
""".trimIndent()

@Service
class ScheduleViewListCommandService(
    private val calendarService: CalendarService
) : Command {

    @PostConstruct
    fun init() {
        val command = "!일정보기"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()
        channel?.let {
            it.createMessage(getResponse(getWords(event))).block()
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {

        return if (contents != null) {
            scheduleViewListResponse.replace("{schedule}", contents["schedule"].toString())
        } else {
            scheduleViewListNotFoundResponse
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        return try {
            val response = calendarService.getCalendars(event.guildId.get().asLong())
            mutableMapOf(Pair("schedule", response))
        } catch (ex: Exception) {
            null
        }
    }
}