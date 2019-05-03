package io.rightpad.daxplorer.data.scalers

import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint

open class SimpleValueDataPointScaler : Scaler<SimpleValueDataPoint, SimpleValueDataPoint, PrimitiveScalerConfig> {
    val valueScaler = PrimitiveScaler()

    override fun configure(config: PrimitiveScalerConfig) {
        this.valueScaler.configure(config)
    }

    override fun buildConfig(): PrimitiveScalerConfig =
            this.valueScaler.buildConfig()

    override fun scale(dataPoint: SimpleValueDataPoint): SimpleValueDataPoint =
            SimpleValueDataPoint(
                    dataPoint.timestamp,
                    this.valueScaler.scale(dataPoint.value)
            )
}
