package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.features.FeatureConfig
import io.rightpad.daxplorer.data.scalers.PrimitiveScalerConfig

data class Config(
        val features: List<FeatureConfig<*, *>>?,
        val scalerConfig: ScalerConfig?
)

data class ScalerConfig(
        val moneyScalerConfig: PrimitiveScalerConfig,
        val volumeScalerConfig: PrimitiveScalerConfig
)
