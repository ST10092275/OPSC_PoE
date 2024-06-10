package com.example.opsc_poe

import java.util.Date

data class Goals(
    val name: String = "",
    val minGoal: Int = 0,
    val maxGoal: Int = 0,
    val date: Date = Date()
) {
    override fun toString(): String {
        return "$name - Min: $minGoal, Max: $maxGoal, Date: $date"
    }
}
