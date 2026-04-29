package com.example.notesappwidget.util

import com.example.notesappwidget.R
import java.util.Calendar

fun getHeadDrawable(creatureId: Int?): Int = when (creatureId) {
    0 -> R.drawable.monsta_head_0
    1 -> R.drawable.monsta_head_1
    2 -> R.drawable.monsta_head_2
    3 -> R.drawable.monsta_head_3
    4 -> R.drawable.monsta_head_4
    5 -> R.drawable.monsta_head_5
    6 -> R.drawable.monsta_head_6
    7 -> R.drawable.monsta_head_7
    8 -> R.drawable.monsta_head_8
    9 -> R.drawable.monsta_head_9
    else -> R.drawable.monsta_head_0
}

fun getFullDrawable(creatureId: Int?): Int = when (creatureId) {
    0 -> R.drawable.monsta_full_0
    1 -> R.drawable.monsta_full_1
    2 -> R.drawable.monsta_full_2
    3 -> R.drawable.monsta_full_3
    4 -> R.drawable.monsta_full_4
    5 -> R.drawable.monsta_full_5
    6 -> R.drawable.monsta_full_6
    7 -> R.drawable.monsta_full_7
    8 -> R.drawable.monsta_full_8
    9 -> R.drawable.monsta_full_9
    else -> R.drawable.monsta_full_0
}

fun getDayBackground(): Int = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY    -> R.drawable.bg_monday
    Calendar.TUESDAY   -> R.drawable.bg_tuesday
    Calendar.WEDNESDAY -> R.drawable.bg_wednesday
    Calendar.THURSDAY  -> R.drawable.bg_thursday
    Calendar.FRIDAY    -> R.drawable.bg_friday
    Calendar.SATURDAY  -> R.drawable.bg_saturday
    else               -> R.drawable.bg_sunday
}
