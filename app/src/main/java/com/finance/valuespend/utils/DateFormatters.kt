package com.finance.valuespend.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val UiDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun LocalDate.toUiDate(): String = format(UiDateFormatter)

