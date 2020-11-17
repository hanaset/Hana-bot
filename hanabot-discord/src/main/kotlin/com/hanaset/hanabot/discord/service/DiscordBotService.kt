package com.hanaset.hanabot.discord.service

import com.hanaset.hanabot.discord.componet.LavaPlayerAudioProvider
import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.exception.DiscordLoginFailedException
import com.hanaset.hanabot.discord.service.command.HelpCommandService
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.GuildCreateEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.discordjson.json.MessageCreateRequest
import discord4j.rest.util.MultipartRequest
import discord4j.voice.AudioProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct


@Service
class DiscordBotService(
        @Value("\${discord.token}") private val discordToken: String,
        private val helpCommandService: HelpCommandService
) {

    private val logger = LoggerFactory.getLogger(DiscordBotService::class.java)
    private val discordClient: GatewayDiscordClient = DiscordClientBuilder.create(discordToken)
            .build()
            .login()
            .onErrorMap { throw DiscordLoginFailedException() }
            .block()!!

    @PostConstruct
    fun init() {
        this.createDispatcher()
    }

    private fun createDispatcher() {

        discordClient.eventDispatcher.on(ReadyEvent::class.java)
                .subscribe { event ->
                    val user = event.self
                    println("Logging in as ${user.username} ${user.discriminator}")
                }

        discordClient.eventDispatcher.on(MessageCreateEvent::class.java)
                .subscribe {

                    logger.info("GuildId: ${it.guildId.get()}, member: ${it.member.get()}, channelId: ${it.message.channelId}, user: ${it.message.author.get()}")
                    val content = it.message.content
                    for (entry in Commands.commands.entries) {
                        if (content.startsWith(entry.key) || content == entry.key) {
                            entry.value.execute(it)
                            break
                        }
                    }
                }

        discordClient.eventDispatcher.on(GuildCreateEvent::class.java)
                .subscribe { event ->
                    val channelId = event.guild.systemChannelId.get().asLong()
                    val text = helpCommandService.getResponse(mapOf("help" to ""))
                    event.client.restClient.channelService.createMessage(channelId, MultipartRequest(MessageCreateRequest.builder().content(text).build())).block()
                }

        discordClient.onDisconnect()
    }


    fun sendMessage(channelId: Long, message: String) {
        discordClient.restClient.channelService.createMessage(channelId, MultipartRequest(MessageCreateRequest.builder().content(message).build())).block()
        logger.info("sendMessage :: ChannelId = $channelId, Message = $message")
    }
}