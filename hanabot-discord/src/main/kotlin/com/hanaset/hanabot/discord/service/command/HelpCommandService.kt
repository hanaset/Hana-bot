package com.hanaset.hanabot.discord.service.command

import com.hanaset.hanabot.discord.constants.Commands
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val helpResponse = """
현재 Hana Bot에서 제공하고 있는 명령어는 아래와 같습니다

** 1. !명령어 **
현재 제공되는 명령어들을 보여드립니다.

** 2. !일정예약 [시간] [제목] **
특정 시간에 스케줄을 예약해주시면 5분전에 알람을 드립니다.😍

[사용법]  !일정예약 yyyy-MM-dd HH:mm:ss 일정제목
[예시]  !일정예약 1994-05-06 00:00:00 하나봇의 생일
""".trimIndent()

@Service
class HelpCommandService : Command {

    @PostConstruct
    fun init() {
        val command = "!명령어"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()
        channel?.let {channel ->
            val words = getWords(event)
            words?.let { channel.createMessage(getResponse(it)).block() }
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return helpResponse
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {
        return mapOf()
    }
}