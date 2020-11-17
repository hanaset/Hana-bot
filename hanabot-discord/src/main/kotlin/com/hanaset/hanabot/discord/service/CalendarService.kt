package com.hanaset.hanabot.discord.service

import com.hanaset.hanabot.common.entity.Calendar
import com.hanaset.hanabot.common.entity.enums.CalendarStatus
import com.hanaset.hanabot.common.repository.CalendarRepository
import com.hanaset.hanabot.discord.exception.DiscordBadRequestException
import com.hanaset.hanabot.discord.exception.DiscordNotFoundCalendarException
import com.hanaset.hanabot.discord.model.CalendarResponse
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class CalendarService(
    private val discordBotService: DiscordBotService,
    private val calendarRepository: CalendarRepository
) {

    private val logger = LoggerFactory.getLogger(CalendarService::class.java)

    fun register(guildId: Long, channelId: Long, userId: Long, title: String, reserveDate: String): String {

        if(title.isEmpty() || title.length > 100) throw DiscordBadRequestException()

        val date = LocalDateTime.parse(reserveDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        if(date.isBefore(LocalDateTime.now())) throw DiscordBadRequestException()

        val calendar = calendarRepository.save(
            Calendar(
                guildId = guildId,
                channelId = channelId,
                userId = userId,
                title = title,
                reserveDate = date
            )
        )

        logger.info("Calender Register :: GuildId = ${calendar.guildId}, Date = $reserveDate, title = $title")

        return "Calendar ID : ${calendar.id}\t[ 일정 ] ${calendar.reserveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 일정 내용 ] ${calendar.title}"
    }

    fun getCalendar(guildId: Long): String {
        val calendars = calendarRepository.findByGuildIdAndStatusOrderByReserveDateAsc(guildId, CalendarStatus.REGISTER)

        if(calendars.isEmpty()) throw DiscordNotFoundCalendarException()

        return calendars.joinToString("\n") {
            val calendarResponse = CalendarResponse.of(it)
            "Calender ID : ${calendarResponse.id}\t[ 일정 ] ${calendarResponse.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 일정 내용 ] ${calendarResponse.title}"
        }
    }

    fun deleteCalender(calendarId: Long): String {
        val calendar = calendarRepository.findByIdOrNull(calendarId) ?: throw DiscordNotFoundCalendarException()

        calendar.cancel()
        calendarRepository.save(calendar)
        return "[ 일정 ] ${calendar.reserveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 일정 내용 ] ${calendar.title}"
    }

    fun alarmBefore10M() {
        val after10M = LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES)
        val calendars = calendarRepository.findByReserveDateAndStatus(after10M, CalendarStatus.REGISTER)
        calendars.forEach {
            discordBotService.sendMessage(it.channelId, "** Calendar ID : ${it.id} [ ${it.title} ] 이/가 10분 뒤 시작됩니다. **")
        }
    }

    fun alarmNow() {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        val calendars = calendarRepository.findByReserveDateAndStatus(now, CalendarStatus.REGISTER)
        calendars.forEach {
            discordBotService.sendMessage(it.channelId, "** 🎤 [ ${it.title} ] 이/가 지금 시작합니다!! **")
            it.complete()
        }

        calendarRepository.saveAll(calendars)

    }
}