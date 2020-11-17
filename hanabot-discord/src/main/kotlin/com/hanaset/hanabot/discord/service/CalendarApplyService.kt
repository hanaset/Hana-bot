package com.hanaset.hanabot.discord.service

import com.hanaset.hanabot.common.entity.CalendarApply
import com.hanaset.hanabot.common.entity.enums.CalendarApplyStatus
import com.hanaset.hanabot.common.repository.CalendarApplyRepository
import com.hanaset.hanabot.common.repository.CalendarRepository
import com.hanaset.hanabot.discord.exception.DiscordBadRequestException
import com.hanaset.hanabot.discord.exception.DiscordNotFoundCalendarException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CalendarApplyService(
    private val calendarRepository: CalendarRepository,
    private val calendarApplyRepository: CalendarApplyRepository
) {

    private val logger = LoggerFactory.getLogger(CalendarApplyService::class.java)

    fun apply(calendarId: Long, userId: Long, username: String, comment: String): String {

        val calendar = calendarRepository.findByIdOrNull(calendarId) ?: throw DiscordNotFoundCalendarException()

        if(comment.isEmpty() ||comment.length > 100) throw DiscordBadRequestException()

        val apply = calendarApplyRepository.findByCalendarIdAndUserId(calendarId, userId ) ?: CalendarApply(
            calendarId = calendarId,
            userId = userId,
            username = username,
            comment = comment
        ).apply {
            this.status = CalendarApplyStatus.APPLY
        }

        calendarApplyRepository.save(apply)
        logger.info("CalendarApply::apply  CalendarId = ${apply.calendarId}, UserId = $userId")

        return "\"${calendar.title}\" [ $username ] 참가 완료"

    }

    fun reject(calendarId: Long, userId: Long, username: String, comment: String): String {

        val calendar = calendarRepository.findByIdOrNull(calendarId) ?: throw DiscordNotFoundCalendarException()

        if(comment.isEmpty() ||comment.length > 100) throw DiscordBadRequestException()

        val apply = calendarApplyRepository.findByCalendarIdAndUserId(calendarId, userId ) ?: CalendarApply(
            calendarId = calendarId,
            userId = userId,
            username = username,
            comment = comment
        ).apply {
            this.status = CalendarApplyStatus.REJECT
        }

        calendarApplyRepository.save(apply)
        logger.info("CalendarApply::reject CalendarId = ${apply.calendarId}, UserId = $userId")

        return "\"${calendar.title}\" [ $username ] 참가 거절 "

    }
}