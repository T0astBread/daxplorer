package io.rightpad.daxplorer.data

import java.time.LocalDateTime

class IndexDataPoint(
        timestamp: LocalDateTime,
        val start: Float,
        val end: Float,
        val min: Float,
        val max: Float,
        val volume: Int,
        var trend: Byte
) : TimeSeriesDataPoint(timestamp) {

    val average: Float
        get() = (this.start + this.end) / 2

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is IndexDataPoint) return false

        if(start != other.start) return false
        if(end != other.end) return false
        if(min != other.min) return false
        if(max != other.max) return false
        if(volume != other.volume) return false
        if(trend != other.trend) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + volume
        result = 31 * result + trend
        return result
    }
}