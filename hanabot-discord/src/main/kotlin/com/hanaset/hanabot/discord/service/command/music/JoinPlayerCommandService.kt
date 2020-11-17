package com.hanaset.hanabot.discord.service.command.music

import com.hanaset.hanabot.discord.componet.TrackScheduler
import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.DiscordMusicService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class JoinPlayerCommandService(
        private val discordMusicService: DiscordMusicService
) : Command {

    @PostConstruct
    fun init() {
        val command = "!음악"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val member = event.member.orElse(null)
        member?.let { member ->
            val voidState = member.voiceState.block()
            voidState?.let { voiceState ->
                val channel = voiceState.channel.block()
                channel?.let {
                    channel.join { it.setProvider(discordMusicService.playerAudioProvider) }.block()
                }
            }
        }

        val trackScheduler = TrackScheduler(discordMusicService.player)
        discordMusicService.playerManager.loadItem(getResponse(getWords(event)), trackScheduler)
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return if(contents == null) {
            ""
        } else {
            contents["music"].toString()
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        return try {
            val words = event.message.content.split(" ")
            val url = words[1]

            mapOf("music" to url)
        } catch (ex: Exception) {
            null
        }
    }
}