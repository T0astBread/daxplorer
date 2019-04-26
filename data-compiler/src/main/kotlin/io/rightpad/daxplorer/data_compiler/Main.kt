package io.rightpad.daxplorer.data_compiler

import com.google.gson.Gson
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.scalers.IndexDataPointScaler
import io.rightpad.daxplorer.data.scalers.Scaler
import io.rightpad.daxplorer.data.serialization.fromCSVToIndexDataPoint
import io.rightpad.daxplorer.data_compiler.scaling.*

class Main {
    companion object {
        private val GSON = Gson()

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

            args.outputFile.delete()

            if(!args.noHeader)
                args.outputFile.appendText(generateHeader(features))

            val compiledData = compile(indexData, features)

            if(args.scaleValues) {
                val scalers = buildScalersList(args)
                initializeScalers(scalers, args, compiledData)
                val scaledData = scale(compiledData, scalers)
                val renderedScaledData = render(scaledData)

                if(args.scaledOutputFile == null)
                    args.outputFile.appendText(renderedScaledData)
                else {
                    args.scaledOutputFile!!.delete()
                    args.scaledOutputFile!!.appendText(renderedScaledData)
                }

                if(args.configOutputFile != null) {
                    val scalerConfig = buildScalerConfiguration(scalers)
                    val config = Config(scalerConfig = scalerConfig)
                    val renderedConfig = GSON.toJson(config)
                    args.configOutputFile!!.delete()
                    args.configOutputFile!!.appendText(renderedConfig)
                }
            }

            val renderedData = render(compiledData)

            if(!args.scaleValues || args.scaledOutputFile != null) {
                args.outputFile.appendText(renderedData)
            }
        }

        private fun buildScalersList(args: Args): List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>> {
            val scalers = mutableListOf(
                    IndexDataPointScaler() as Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>
            )
            return scalers
        }

        private fun initializeScalers(
                scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>>,
                args: Args,
                compiledData: List<List<TimeSeriesDataPoint?>>
        ) {
            val createNewScalerConfig = args.scaleParams == null
            println(if(createNewScalerConfig) "Creating new scaler config" else "Reading scaler config from file")

            val config = if(createNewScalerConfig)
                createConfigFromData(compiledData)
            else
                GSON.fromJson<ScalerConfig>(args.scaleParams!!.reader(), ScalerConfig::class.java)

            if(config != null)
                applyScalerConfiguration(config, scalers)
        }
    }
}