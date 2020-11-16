package com.hanaset.hanabot.discord.service.command

import com.hanaset.hanabot.discord.constants.Commands
import discord4j.core.event.domain.message.MessageCreateEvent
import javax.annotation.PostConstruct

val scheduleReserveResponse = """
** {date}일자로 {title}이/가 예약 되었습니다. **
🥰 일정 시작 전 5분 예약해주신 채널로 알람 드리겠습니다.^^
""".trimIndent()

val scheduleReserveFailedResponse = """
명령어의 형식이 틀렸습니다.
** yyyy-MM-dd HH:mm:ss [일정 내용 (최대 100자)]**
(예시: 1994-05-06 00:01:50 오늘 저녁 8시에 정모있습니다!!!!) 
""".trimIndent()

class ScheduleReserveCommandService : Command {

    @PostConstruct
    fun init() {
        val command = "!일정예약"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {

    }

    override fun getResponse(contents: Map<String, String>?): String {
        TODO("Not yet implemented")
    }

    override fun valid(message: String): Boolean {

        try {

        } catch (ex: Exception) {
            return false
        }

    }


}