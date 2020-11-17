package com.hanaset.hanabot.discord.service.command

import discord4j.core.event.domain.message.MessageCreateEvent

interface Command {
    fun execute(event: MessageCreateEvent)
    fun getResponse(contents: Map<String, String>?): String
    fun getWords(event: MessageCreateEvent): Map<String, String>?
}