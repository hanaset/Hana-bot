package com.hanaset.hanabot.common.entity

import com.hanaset.hanabot.common.entity.enums.CalendarStatus
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "calendar")
class Calendar(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "guild_id")
    val guildId: Long,

    @Column(name = "channel_id")
    val channelId: Long,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "reserve_date")
    val reserveDate: LocalDateTime,

    val title: String,

    @Enumerated(EnumType.STRING)
    var status: CalendarStatus = CalendarStatus.REGISTER
): AbstractBaseAuditEntity() {

    fun cancel() {
        this.status = CalendarStatus.CANCEL
    }

    fun complete() {
        this.status = CalendarStatus.COMPLETE
    }
}