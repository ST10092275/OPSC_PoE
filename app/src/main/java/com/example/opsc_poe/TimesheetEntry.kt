package com.example.opsc_poe

import com.google.firebase.Timestamp
import com.google.firebase.database.core.utilities.ParsedUrl
import com.google.type.Date
import java.util.concurrent.TimeUnit

data class TimesheetEntry(
    val id: String = "",
    val date: Timestamp = Timestamp.now(),
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val description: String = "",
    val category: String = "",
    val photoUrl: String? = null

){
    fun getTotalHours(): Long {
        val diff = endTime.seconds - startTime.seconds
        return TimeUnit.SECONDS.toHours(diff)
    }
}
