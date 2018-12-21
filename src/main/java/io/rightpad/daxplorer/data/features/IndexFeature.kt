package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.IndexDataPoint

class IndexFeature : Feature<IndexDataPoint> {
    var mutableFeatureDataRef: List<IndexDataPoint>? = null
    override val featureData: List<IndexDataPoint>
        get() = mutableFeatureDataRef ?: listOf()

    override fun calculate(indexData: List<IndexDataPoint>) {
        this.mutableFeatureDataRef = indexData
    }
}