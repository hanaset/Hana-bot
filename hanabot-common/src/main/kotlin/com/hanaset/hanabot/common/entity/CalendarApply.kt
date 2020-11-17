package com.hanaset.hanabot.common.entity

import com.hanaset.hanabot.common.entity.enums.CalendarApplyStatus
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "calendar_apply")
class CalendarApply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "calendar_id")
    val calendarId: Long,

    @Column(name = "user_id")
    val userId: Long,

    val username: String,

    val comment: String,

    @Enumerated(EnumType.STRING)
    var status: CalendarApplyStatus = CalendarApplyStatus.APPLY
): AbstractBaseAuditEntity() {
    fun attend() {
        this.status = CalendarApplyStatus.ATTEND
    }

    fun nonAttend() {
        this.status = CalendarApplyStatus.NON_ATTEND
    }

    fun reject() {
        this.status = CalendarApplyStatus.REJECT
    }

    fun apply() {
        this.status = CalendarApplyStatus.APPLY
    }
}