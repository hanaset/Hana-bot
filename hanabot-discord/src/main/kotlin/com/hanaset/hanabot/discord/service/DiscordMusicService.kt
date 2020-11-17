package com.hanaset.hanabot.discord.service

import com.hanaset.hanabot.discord.componet.LavaPlayerAudioProvider
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.voice.AudioProvider
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class DiscordMusicService {

    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val player = createPlayer()
    val playerAudioProvider = createAudioProvider()

    private fun createAudioProvider(): AudioProvider {
        return LavaPlayerAudioProvider(player)
    }

    private fun createPlayer(): AudioPlayer {
        playerManager.configuration.frameBufferFactory = AudioFrameBufferFactory { bufferDuration: Int, format: AudioDataFormat?, stopping: AtomicBoolean? -> NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping) }
        AudioSourceManagers.registerRemoteSources(playerManager)
        return playerManager.createPlayer()
    }
}