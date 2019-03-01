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
            .default { File(
                    this.inputFile.parentFile,
                    "compiled_${this.inputFile.nameWithoutExtension}.csv"
            ) }
}
