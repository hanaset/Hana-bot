package com.hanaset.hanabot.discord

import com.hanaset.hanabot.common.config.HanaJpaDatabaseConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.ApplicationPidFileWriter
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import javax.annotation.PostConstruct

@EnableScheduling
@SpringBootApplication(scanBasePackages = [
    "com.hanaset.hanabot.common.*",
    "com.hanaset.hanabot.discord.*"
])
@Import(HanaJpaDatabaseConfig::class)
class HanabotDiscordApplication(
        private val buildProperties: BuildProperties,
        private val environment: Environment
) : ApplicationListener<ApplicationReadyEvent> {

    private val logger = LoggerFactory.getLogger(HanabotDiscordApplication::class.java)

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("{} applicationReady, profiles = {}", buildProperties.name, environment.activeProfiles.contentToString())
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(HanabotDiscordApplication::class.java)
            .listeners(ApplicationPidFileWriter())
            .run(*args)
}