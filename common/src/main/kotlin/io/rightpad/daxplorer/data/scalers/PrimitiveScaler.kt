package io.rightpad.daxplorer.data.scalers

class PrimitiveScaler(var min: Float? = null, var max: Float? = null): Scaler<Float, Float> {

    override fun ingest(dataPoint: Float) {
        if(this.max == null)
            this.max = dataPoint

        if(this.min == null || dataPoint < this.min!!)
            this.min = dataPoint
        else if(dataPoint > this.max!!)
            this.max = dataPoint
    }

    fun ingest(dataPoint: Int) = ingest(dataPoint.toFloat())

    override fun scale(dataPoint: Float): Float =
            if(this.min != null && this.max != null)
                (dataPoint - this.min!!) / (this.max!! - this.min!!)
            else
                throw IllegalStateException("You first have to initialize the scaler by either digesting a value or setting min and max directly")

    fun scale(dataPoint: Int): Float = scale(dataPoint.toFloat())
}
