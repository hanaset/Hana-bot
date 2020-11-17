package com.hanaset.hanabot.discord.scheduler

import com.hanaset.hanabot.discord.service.CalendarService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CalendarScheduler(
    private val calendarService: CalendarService
) {

    @Scheduled(cron = "1 * * * * *", zone = "Asia/Seoul")
    fun alarm() {
        calendarService.alarmBefore10M()
        calendarService.alarmNow()
    }
}