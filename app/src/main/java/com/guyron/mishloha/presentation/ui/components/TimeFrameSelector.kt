package com.guyron.mishloha.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.guyron.mishloha.domain.models.TimeFrame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFrameSelector(
    selectedTimeFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeFrame.entries.forEach { timeFrame ->
            FilterChip(
                onClick = { onTimeFrameSelected(timeFrame) },
                label = { Text(timeFrame.displayName) },
                selected = selectedTimeFrame == timeFrame,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
