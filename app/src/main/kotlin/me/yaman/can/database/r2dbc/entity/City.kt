package me.yaman.can.database.r2dbc.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Table
@Entity
data class City(
    @Id
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long?,
    val name:String,
    val country:String
)
