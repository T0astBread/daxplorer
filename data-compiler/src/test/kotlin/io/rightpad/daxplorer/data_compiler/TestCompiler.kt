package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.IndexFeature
import io.rightpad.daxplorer.data.serialization.csv.IndexDataCSVSerializer
import org.junit.Test

class TestCompiler {
    private val testFeatures = listOf<Compiler.FeatureConfig<TimeSeriesDataPoint, String>>(
            Compiler.FeatureConfig(IndexFeature(), IndexDataCSVSerializer())
    )

    @Test
    fun testCompiler() {
        val compiler = io.rightpad.daxplorer.data_compiler.Compiler<String>(
                featureColumns = testFeatures,
                mergeColumns = { it.joinToString("\n") }
        )
    }
}