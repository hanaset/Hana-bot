package com.hanaset.hanabot.discord.service.command.schedule

import com.hanaset.hanabot.discord.constants.Commands
import com.hanaset.hanabot.discord.service.CalendarService
import com.hanaset.hanabot.discord.service.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

var scheduleRegisterResponse = """
** {schedule} 이/가 예약 되었습니다. **

🥰 일정 시작 전 10분 예약해주신 채널로 알람 드리겠습니다.^^

🌐 참여 하실 분들은 "!참가신청 [Calender ID] [추가사항 (100글자이하)]" 를 쳐주세요!!
❌ 참여가 힘드신 분들은 "!참가거절 [Calendar ID] [추가사항 (100글자이하)]" 를 쳐주세요!!
""".trimIndent()

val scheduleRegisterFailedResponse = """
명령어의 형식이 틀렷거나 이미 지난 날짜입니다.
** yyyy-MM-dd HH:mm [ 일정 내용 (최대 100자) ]**

(예시: 2020-11-20 20:30 오늘 저녁 8시에 정모있습니다!!!!) 
""".trimIndent()

@Service
class ScheduleRegisterCommandService(
    private val calendarService: CalendarService
) : Command {

    @PostConstruct
    fun init() {
        val command = "!일정추가"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()
        channel?.let {
            it.createMessage(getResponse(getWords(event))).block()
        }

    }

    override fun getResponse(contents: Map<String, String>?): String {
        return if(contents == null)
            scheduleRegisterFailedResponse
        else {
            scheduleRegisterResponse.replace("{schedule}", contents["schedule"].toString())
        }
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {

        try {
            val words = event.message.content.split(" ")
            val localDateTime = LocalDateTime.parse("${words[1]} ${words[2]}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            val title = words.subList(3, words.size).joinToString(" ")
            val date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

            val res = calendarService.register(
                guildId = event.guildId.get().asLong(),
                channelId = event.message.channelId.asLong(),
                userId = event.message.author.get().id.asLong(),
                title = title,
                reserveDate = date
            )

            return mutableMapOf("schedule" to res)
        }catch (ex: Exception) {
            return null
        }
    }


}