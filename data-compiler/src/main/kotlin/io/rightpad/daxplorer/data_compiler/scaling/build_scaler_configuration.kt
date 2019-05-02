package io.rightpad.daxplorer.data_compiler.scaling

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.scalers.IndexDataPointScaler
import io.rightpad.daxplorer.data.scalers.IndexDataPointScalerConfig
import io.rightpad.daxplorer.data.scalers.PrimitiveScalerConfig
import io.rightpad.daxplorer.data.scalers.Scaler
import io.rightpad.daxplorer.data_compiler.ScalerConfig

fun buildScalerConfiguration(
        scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>>
): ScalerConfig {
    val indexDataPointScalerConfigs = scalers.mapNotNull { it as? IndexDataPointScaler }
            .map { scaler -> scaler.buildConfig() }
    return ScalerConfig(
            moneyScalerConfig = indexDataPointScalerConfigs
                    .map { config -> config.toMoneyConfig() }
                    .merge(),
            volumeScalerConfig = indexDataPointScalerConfigs
                    .map { config -> config.volumeScalerConfig }
                    .merge()
    )
}

fun IndexDataPointScalerConfig.toMoneyConfig(): PrimitiveScalerConfig =
        merge(this.startScalerConfig, this.endScalerConfig, this.minScalerConfig, this.maxScalerConfig)

private fun List<PrimitiveScalerConfig>.merge() = merge(*this.toTypedArray())

private fun merge(vararg configs: PrimitiveScalerConfig) =
        PrimitiveScalerConfig(
                min = configs.map { it.min }.min() ?: 0f,
                max = configs.map { it.max }.max() ?: 0f
        )
