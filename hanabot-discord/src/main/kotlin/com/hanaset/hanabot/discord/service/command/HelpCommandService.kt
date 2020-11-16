package com.hanaset.hanabot.discord.service.command

import com.hanaset.hanabot.discord.constants.Commands
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val helpResponse = """
현재 Hana Bot에서 제공하고 있는 명령어는 아래와 같습니다

1. !명령어
- 현재 제공되는 명령어들을 보여드립니다.
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

        if(valid(event.message.content)) {
            channel?.let { it.createMessage(getResponse(null)).block() }
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return helpResponse
    }

    override fun valid(message: String): Boolean {
        return true
    }
}