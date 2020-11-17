package com.hanaset.hanabot.common.repository

import com.hanaset.hanabot.common.entity.Calendar
import com.hanaset.hanabot.common.entity.enums.CalendarStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CalendarRepository : JpaRepository<Calendar, Long> {

    fun findByReserveDateAndStatus(date: LocalDateTime, status: CalendarStatus): List<Calendar>

    fun findByGuildIdAndStatusOrderByReserveDateAsc(guildId: Long, status: CalendarStatus): List<Calendar>
}