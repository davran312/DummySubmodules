package namba.wallet.nambaone.common.date

import android.content.Context
import java.util.Locale
import kotlin.math.ceil
import namba.wallet.nambaone.core.R
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAccessor

private val dayMonthFormatter = DateTimeFormatter.ofPattern("dd MMMM")
private val dayMonthYearFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")

fun ZonedDateTime.toDateString(context: Context): String {
    val day = withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
    val today = Today.value
    return when {
        day.isEqual(today) -> context.getString(R.string.today)
        today.year == day.year -> dayMonthFormatter.formatWithLocale(day)
        else -> dayMonthYearFormatter.formatWithLocale(day)
    }
}

fun ZonedDateTime.toDateTimeString(): String {
    val localDateTime = withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    return dateTimeFormatter.formatWithLocale(localDateTime)
}

fun ZonedDateTime.isSameDayAs(other: ZonedDateTime) =
    toLocalDate() == other.toLocalDate()

fun Duration.ceilToMinutes() =
    ceil(seconds.toFloat() / ChronoUnit.MINUTES.duration.seconds).toInt()

private fun DateTimeFormatter.formatWithLocale(temporal: TemporalAccessor) =
    withLocale(Locale.getDefault()).format(temporal)
