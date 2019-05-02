package io.rightpad.daxplorer.data_compiler

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.io.File

class Args(parser: ArgParser) {
    val inputFile by parser.positional(
            "INPUT",
            "The file to read the source data from"
    ) { File(this) }

    val outputFile by parser.storing(
            "-o", "--output",
            help = "The file to write the compiled data to"
    ) { File(this) }
            .default {
                File(
                        this.inputFile.parentFile,
                        "compiled_${this.inputFile.nameWithoutExtension}.csv"
                )
            }

    val noHeader by parser.flagging(
            "-H", "--no-header",
            help = "If set, ommits the header row (the first row containing the names of the columns)"
    )

    val averages by parser.storing(
            "--averages",
            help = "The average columns to add, specified as their spans, comma seperated. For example: --averages 50,100,200"
    ) {
        splitToSequence(',')
                .map { it.toInt() }
    }.default(emptySequence<Int>())

    val scaleValues by parser.flagging(
            "-s", "--scale",
            help = "If set, scalable values are scaled between the minimum and the maximum either read from the source data or the file specified with the --scale-params argument"
    )

    val scaleParams by parser.storing(
            "--scale-params",
            help = "The file to read the parameters for value scaling from"
    ) { File(this) }
            .default<File?>(null)

    val scaledOutputFile by parser.storing(
            "--scaled-output",
            help = "If set, outputs absolute values to the regular output file and scaled values to the specified file"
    ) { File(this) }
            .default<File?>(null)

    val configOutputFile by parser.storing(
            "--config-output",
            help = "The file to write the config to"
    ) { File(this) }
            .default<File?>(null)
}
