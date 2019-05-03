package io.rightpad.daxplorer.data_compiler

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.scalers.IndexDataPointScaler
import io.rightpad.daxplorer.data.scalers.Scaler
import io.rightpad.daxplorer.data.serialization.fromCSVToIndexDataPoint
import io.rightpad.daxplorer.data_compiler.scaling.applyScalerConfiguration
import io.rightpad.daxplorer.data_compiler.scaling.buildScalerConfiguration
import io.rightpad.daxplorer.data_compiler.scaling.createConfigFromData
import io.rightpad.daxplorer.data_compiler.scaling.scale

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

            val features = args.config.features
                    ?.map { it.createFeature() as Feature<TimeSeriesDataPoint> } ?: listOf()

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

                if(args.configOutputFile == null) {
                    println("Saving config file to ${args.configOutputFile}")

                    val scalerConfig = buildScalerConfiguration(scalers)

                    val adjustedConfig = Config(args.config.features, scalerConfig)
                    val renderedConfig = GSON.toJson(adjustedConfig)

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
            val featureColumnScalers = args.config.features?.map {
                it.createScaler() as Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>
            } ?: listOf()

            return listOf(IndexDataPointScaler() as Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>)
                    .plus(featureColumnScalers)
        }

        private fun initializeScalers(
                scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>>,
                args: Args,
                compiledData: List<List<TimeSeriesDataPoint?>>
        ) {
            val createNewScalerConfig = args.config.scalerConfig == null
            println(if(createNewScalerConfig) "Creating new scaler config" else "Reading scaler config from file")

            val config =
                    if(createNewScalerConfig) createConfigFromData(compiledData)
                    else args.config.scalerConfig

            if(config != null)
                applyScalerConfiguration(config, scalers)
        }
    }
}