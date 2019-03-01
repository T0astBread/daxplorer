package io.rightpad.daxplorer.data_compiler

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            val args = ArgParser(args).parseInto(::Args)

            println(args.inputFile)
            println(args.outputFile)
        }
    }
}