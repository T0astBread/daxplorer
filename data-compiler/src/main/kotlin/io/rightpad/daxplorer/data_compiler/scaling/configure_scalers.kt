package io.rightpad.daxplorer.data_compiler.scaling

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.scalers.*
import io.rightpad.daxplorer.data_compiler.ScalerConfig

fun applyScalerConfiguration(
        config: ScalerConfig,
        scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>?>
) {
    applyIndexDataPointScalerConfig(config, scalers)
    applyMoneyScalerConfig(config, scalers)
}

private fun applyIndexDataPointScalerConfig(
        config: ScalerConfig,
        scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>?>
) {
    val indexDataPointScalerConfig = IndexDataPointScalerConfig(
            startScalerConfig = config.moneyScalerConfig,
            endScalerConfig = config.moneyScalerConfig,
            minScalerConfig = config.moneyScalerConfig,
            maxScalerConfig = config.moneyScalerConfig,
            volumeScalerConfig = config.volumeScalerConfig
    )
    scalers.mapNotNull { it as? IndexDataPointScaler }
            .forEach { it.configure(indexDataPointScalerConfig) }
}

fun applyMoneyScalerConfig(
        config: ScalerConfig,
        scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>?>
) {
    scalers.mapNotNull { it as? AverageDataPointScaler }
            .forEach { it.configure(config.moneyScalerConfig) }
}
