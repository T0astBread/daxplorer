package io.rightpad.daxplorer.data.scalers

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.relative.RelativeIndexDataPoint

class IndexDataPointScaler: Scaler<IndexDataPoint, RelativeIndexDataPoint, IndexDataPointScalerConfig> {
    private val startScaler = PrimitiveScaler()
    private val endScaler = PrimitiveScaler()
    private val minScaler = PrimitiveScaler()
    private val maxScaler = PrimitiveScaler()
    private val volumeScaler = PrimitiveScaler()

    override fun configure(config: IndexDataPointScalerConfig) {
        this.startScaler.configure(config.startScalerConfig)
        this.endScaler.configure(config.endScalerConfig)
        this.minScaler.configure(config.minScalerConfig)
        this.maxScaler.configure(config.maxScalerConfig)
        this.volumeScaler.configure(config.volumeScalerConfig)
    }

    override fun buildConfig(): IndexDataPointScalerConfig =
            IndexDataPointScalerConfig(
                    this.startScaler.buildConfig(),
                    this.endScaler.buildConfig(),
                    this.minScaler.buildConfig(),
                    this.maxScaler.buildConfig(),
                    this.volumeScaler.buildConfig()
            )

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

data class IndexDataPointScalerConfig(
        val startScalerConfig: PrimitiveScalerConfig,
        val endScalerConfig: PrimitiveScalerConfig,
        val minScalerConfig: PrimitiveScalerConfig,
        val maxScalerConfig: PrimitiveScalerConfig,
        val volumeScalerConfig: PrimitiveScalerConfig
)
