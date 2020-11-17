package com.hanaset.hanabot.common.repository

import com.hanaset.hanabot.common.entity.CalendarApply
import com.hanaset.hanabot.common.entity.enums.CalendarApplyStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarApplyRepository: JpaRepository<CalendarApply, Long> {

    fun findByCalendarIdAndUserId(calendarId: Long, userId: Long): CalendarApply?
    fun findByCalendarIdAndStatus(calendarId: Long, status: CalendarApplyStatus): List<CalendarApply>
    fun findByCalendarId(calendarId: Long): List<CalendarApply>
}