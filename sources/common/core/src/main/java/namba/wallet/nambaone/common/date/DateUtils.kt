package namba.wallet.nambaone.common.date

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

object DateUtils {
    fun zonedDateTime(millis: Long, zoneId: ZoneId = ZoneId.of("UTC")): ZonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId)
}
