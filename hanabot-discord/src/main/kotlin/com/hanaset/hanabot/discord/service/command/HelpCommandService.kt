package com.hanaset.hanabot.discord.service.command

import com.hanaset.hanabot.discord.constants.Commands
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

val helpResponse = """
Hana Bot에서 지원하는 기능은
1. 일정 관리 및 참가 및 불참 기능

현재 Hana Bot에서 제공하고 있는 명령어는 아래와 같습니다.

** 1. 명령어 **
- __ 현재 제공되는 명령어들을 보여드립니다. __
`[사용법] !명령어`

** <2. 일정 관련 명령어> ** 
2-1. 일정추가
- __ 특정 시간에 스케줄을 예약해주시면 10분전에 알람을 드립니다.😍 __
`[사용법] !일정예약 yyyy-MM-dd HH:mm 일정제목`
`[예시] !일정예약 1994-05-06 00:00 하나봇의 생일`

2-2. 일정보기
- __ 등록 된 일정들에 대해 목록을 보여드립니다. __
`[사용법] !일정보기`

2-3. 참가인원확인
- __ 특정 일정에 참가한 인원들을 확인할 수 있습니다. __
`[사용법] !참가인원확인 [Calendar ID]`

2-4. 일정삭제
- __ 등록하신 일정을 삭제 할 수 있습니다. __
`[사용법] !일정삭제 [Calendar ID]`

2-5. 참가신청
- __ 등록된 일정에 참가 신청을 할 수 있습니다. __
`[사용법] !참가신청 [Calendar ID] [코멘트 (100자 이하)]`

2-6. 참가거절
- __ 등록된 일정에 참여 불가를 신청 할 수 있습니다. __
`[사용법] !참가거절 [Calendar ID] [코멘트 (100자 이하)]`
""".trimIndent()

@Service
class HelpCommandService : Command {

    @PostConstruct
    fun init() {
        val command = "!명령어"
        Commands.commands[command] = this
    }

    override fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.block()
        channel?.let {channel ->
            val words = getWords(event)
            words?.let { channel.createMessage(getResponse(it)).block() }
        }
    }

    override fun getResponse(contents: Map<String, String>?): String {
        return helpResponse
    }

    override fun getWords(event: MessageCreateEvent): Map<String, String>? {
        return mapOf()
    }
}