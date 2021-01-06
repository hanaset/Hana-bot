package com.hanaset.hanabot.discord.exception

open class HanabotDiscordException(
        val code: ErrorCode,
        override val message: String? = null
) : RuntimeException(
        message ?: code.message
)

class DiscordLoginFailedException: HanabotDiscordException(ErrorCode.LOGIN_FAILED)
class DiscordNotFoundCalendarException: HanabotDiscordException(ErrorCode.NOT_FOUND)
class DiscordBadRequestException: HanabotDiscordException(ErrorCode.BAD_REQUEST)

