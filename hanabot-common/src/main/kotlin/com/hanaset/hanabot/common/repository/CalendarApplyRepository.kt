package com.hanaset.hanabot.common.repository

import com.hanaset.hanabot.common.entity.CalendarApply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarApplyRepository: JpaRepository<CalendarApply, Long> {

    fun findByCalendarIdAndUserId(calendarId: Long, userId: Long): CalendarApply?
}