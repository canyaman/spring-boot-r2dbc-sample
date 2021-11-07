package me.yaman.can.database.r2dbc.service

import me.yaman.can.database.r2dbc.entity.City
import me.yaman.can.database.r2dbc.repository.CityRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CityService(val cityRepository: CityRepository) {
    fun getCity(name:String): Mono<City?> {
        return cityRepository.findByName(name)
    }
}