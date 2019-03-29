package io.rightpad.daxplorer.data.scalers

interface Scaler<A, R> {
    fun ingest(dataPoint: A)
    fun scale(dataPoint: A): R
}