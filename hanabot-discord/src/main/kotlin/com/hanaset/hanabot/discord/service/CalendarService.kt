package com.hanaset.hanabot.discord.service

import com.hanaset.hanabot.common.entity.Calendar
import com.hanaset.hanabot.common.entity.enums.CalendarApplyStatus
import com.hanaset.hanabot.common.entity.enums.CalendarStatus
import com.hanaset.hanabot.common.repository.CalendarApplyRepository
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
    private val calendarRepository: CalendarRepository,
    private val calendarApplyRepository: CalendarApplyRepository
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

        return "Calendar ID : ${calendar.id}\t[ 일정 ] ${calendar.reserveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 내용 ] ${calendar.title}"
    }

    fun getCalendars(guildId: Long): String {
        val calendars = calendarRepository.findByGuildIdAndStatusOrderByReserveDateAsc(guildId, CalendarStatus.REGISTER)

        if(calendars.isEmpty()) throw DiscordNotFoundCalendarException()

        return calendars.joinToString("\n") {
            val calendarResponse = CalendarResponse.of(it)
            val users = calendarApplyRepository.findByCalendarId(it.id)
            val applyUsers = users.filter { user -> user.status == CalendarApplyStatus.APPLY }
            val rejectUsers = users.filter { user -> user.status == CalendarApplyStatus.REJECT }
            "Calender ID : ${calendarResponse.id}\t[ 일정 ] ${calendarResponse.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 내용 ] ${calendarResponse.title} ==> 신청 인원 : ${applyUsers.size}명 / 거절 인원 : ${rejectUsers.size}명"
        }
    }

    fun getCalendar(calendarId: Long, guildId: Long): String {

        val calendar = calendarRepository.findByGuildIdAndChannelIdAndStatus(calendarId = calendarId, guildId = guildId, status = CalendarStatus.REGISTER) ?: throw DiscordBadRequestException()
        val users = calendarApplyRepository.findByCalendarId(calendarId)
        val appliedUser = users.filter { it.status == CalendarApplyStatus.APPLY }
        val rejectUser = users.filter { it.status == CalendarApplyStatus.REJECT }

        val applyUserText = appliedUser.joinToString(", ") { user -> user.username }
        val rejectUserText = rejectUser.joinToString(", ") { user -> user.username }

        return  """
            Calendar ID : ${calendar.id}
            일정 : ${calendar.reserveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
            내용 : ${calendar.title}
            
            참가인원 : ${appliedUser.size} / 불참인원 : ${rejectUser.size}
            참가자 명단 : $applyUserText
            불참자 명단 : $rejectUserText
        """.trimIndent()

    }

    fun deleteCalender(calendarId: Long): String {
        val calendar = calendarRepository.findByIdOrNull(calendarId) ?: throw DiscordNotFoundCalendarException()

        calendar.cancel()
        calendarRepository.save(calendar)
        return "[ 일정 ] ${calendar.reserveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\t[ 내용 ] ${calendar.title}"
    }

    fun alarmBefore10M() {
        val after10M = LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES)
        val calendars = calendarRepository.findByReserveDateAndStatus(after10M, CalendarStatus.REGISTER)
        calendars.forEach { it ->
            val users = calendarApplyRepository.findByCalendarIdAndStatus(it.id, CalendarApplyStatus.APPLY)
            val applyUsers = users.filter { user -> user.status == CalendarApplyStatus.APPLY }
            val rejectUsers = users.filter { user -> user.status == CalendarApplyStatus.REJECT }

            val applyUserText = applyUsers.joinToString(", ") { user -> user.username }
            val rejectUserText = rejectUsers.joinToString(", ") { user -> user.username }

            val text = """
                ** Calendar ID : ${it.id} [ ${it.title} ] 이/가 10분 뒤 시작됩니다. **
                
                참가인원 : ${applyUsers.size} / 불참인원 : ${rejectUsers.size}
                참가자 명단 : $applyUserText
                불참자 명단 : $rejectUserText
            """.trimIndent()
            discordBotService.sendMessage(it.channelId, text)
        }
    }

    fun alarmNow() {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        val calendars = calendarRepository.findByReserveDateAndStatus(now, CalendarStatus.REGISTER)
        calendars.forEach {
            val users = calendarApplyRepository.findByCalendarId(it.id)
            val applyUsers = users.filter { user -> user.status == CalendarApplyStatus.APPLY }
            val rejectUsers = users.filter { user -> user.status == CalendarApplyStatus.REJECT }

            val applyUserText = applyUsers.joinToString(", ") { user -> user.username }
            val rejectUserText = rejectUsers.joinToString(", ") { user -> user.username }

            val text = """
                ** 🎤 [ ${it.title} ] 이/가 지금 시작합니다!! **
                
                참가인원 : ${applyUsers.size} / 불참인원 : ${rejectUsers.size}
                참가자 명단 : $applyUserText
                불참자 명단 : $rejectUserText
            """.trimIndent()

            discordBotService.sendMessage(it.channelId, text)
            it.complete()
        }

        calendarRepository.saveAll(calendars)

    }
}