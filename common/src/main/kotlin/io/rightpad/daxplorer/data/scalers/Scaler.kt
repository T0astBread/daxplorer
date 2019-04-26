package io.rightpad.daxplorer.data.scalers

interface Scaler<A, R, C> {
    fun ingest(dataPoint: A)

    fun configure(config: C)
    fun buildConfig(): C

    fun scale(dataPoint: A): R
}