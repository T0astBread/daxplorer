package io.rightpad.daxplorer.data.scalers

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.relative.RelativeIndexDataPoint

class IndexDataPointScaler : Scaler<IndexDataPoint, RelativeIndexDataPoint> {
    private val startScaler = PrimitiveScaler()
    private val endScaler = PrimitiveScaler()
    private val minScaler = PrimitiveScaler()
    private val maxScaler = PrimitiveScaler()
    private val volumeScaler = PrimitiveScaler()

    override fun ingest(dataPoint: IndexDataPoint) {
        this.startScaler.ingest(dataPoint.start)
        this.endScaler.ingest(dataPoint.end)
        this.minScaler.ingest(dataPoint.min)
        this.maxScaler.ingest(dataPoint.max)
        this.volumeScaler.ingest(dataPoint.volume)
    }

    override fun scale(dataPoint: IndexDataPoint): RelativeIndexDataPoint =
            RelativeIndexDataPoint(
                    dataPoint.timestamp,
                    this.startScaler.scale(dataPoint.start),
                    this.endScaler.scale(dataPoint.end),
                    this.minScaler.scale(dataPoint.min),
                    this.maxScaler.scale(dataPoint.max),
                    this.volumeScaler.scale(dataPoint.volume),
                    dataPoint.trend
            )
}
