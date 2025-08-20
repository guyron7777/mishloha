package com.guyron.mishloha.data.utils

import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.data.Constants
import java.text.SimpleDateFormat
import java.util.*

object QueryUtils {
    
    fun buildTimeFrameQuery(timeFrame: TimeFrame): String {
        val dateFormat = SimpleDateFormat(Constants.SEARCH_DATE_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        val endDate = dateFormat.format(calendar.time)
        
        when (timeFrame) {
            TimeFrame.DAY -> calendar.add(Calendar.DAY_OF_MONTH, -1)
            TimeFrame.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            TimeFrame.MONTH -> calendar.add(Calendar.MONTH, -1)
        }
        
        val startDate = dateFormat.format(calendar.time)
        
        return "created:$startDate..$endDate"
    }
}
