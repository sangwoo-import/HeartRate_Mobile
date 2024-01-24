package com.example.saloris.Database.SmartwatchHeart

import java.time.Instant
import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement

@Measurement(name = "heart_watch")
data class SmartHeart(
//    @Column(tag = true) val _user: String,
    @Column val value: Int?,
    @Column(timestamp = true) val time: Instant
) {
    constructor(value: Int) : this(value, Instant.now())
}


