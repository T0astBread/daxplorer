package io.rightpad.daxplorer.data_compiler

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.serialization.fromCSVToIndexDataPoint

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            val args = ArgParser(args).parseInto(::Args)

            println(args.inputFile)
            println(args.outputFile)

            val indexData = args.inputFile
                    .readLines()
                    .map { it.fromCSVToIndexDataPoint() }

            val features = mutableListOf<Feature<TimeSeriesDataPoint>>()
            args.averages
                    .map { span -> AverageFeature(span) as Feature<TimeSeriesDataPoint> }
                    .forEach { features.add(it) }

            val compiledData = compile(indexData, features)
            args.outputFile.writeText(compiledData)
        }
    }
}