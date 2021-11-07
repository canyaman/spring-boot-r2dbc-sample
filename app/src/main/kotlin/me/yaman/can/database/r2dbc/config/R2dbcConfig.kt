package me.yaman.can.database.r2dbc.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["me.yaman.can.database.r2dbc.repository"])
class R2dbcConfig(){
}