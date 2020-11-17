package com.hanaset.hanabot.discord.model

import com.hanaset.hanabot.common.entity.Calendar
import java.time.LocalDateTime

data class CalendarResponse(
    val id: Long,
    val title: String,
    val date: LocalDateTime
) {
    companion object {
        fun of(calendar: Calendar): CalendarResponse {
            return CalendarResponse(
                id = calendar.id,
                title = calendar.title,
                date = calendar.reserveDate
            )
        }
    }
}