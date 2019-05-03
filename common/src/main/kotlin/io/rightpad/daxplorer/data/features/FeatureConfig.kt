package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.scalers.Scaler

interface FeatureConfig<
        F: Feature<*>,
        S: Scaler<*, *, *>?> {
    fun createFeature(): F
    fun createScaler(): S
}