package com.guyron.mishloha.domain.models

enum class TimeFrame(val displayName: String, val query: String) {
    DAY("Last Day", "daily"),
    WEEK("Last Week", "weekly"),
    MONTH("Last Month", "monthly")
}
