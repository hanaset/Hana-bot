package com.hanaset.hanabot.discord.exception

open class HanabotDiscordException(
        val code: ErrorCode,
        override val message: String? = null
) : RuntimeException(
        message ?: code.message
)

class HanabotDiscordLoginFailedException: HanabotDiscordException(ErrorCode.LOGIN_FAILED)
