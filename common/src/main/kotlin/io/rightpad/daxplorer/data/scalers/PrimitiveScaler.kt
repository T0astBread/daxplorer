package io.rightpad.daxplorer.data.scalers

class PrimitiveScaler(var min: Float? = null, var max: Float? = null): Scaler<Float, Float, PrimitiveScalerConfig> {

    override fun configure(config: PrimitiveScalerConfig) {
        this.min = config.min
        this.max = config.max
    }

    override fun buildConfig() =
            if(this.min == null || this.max == null)
                throwUninitializedException()
            else
                PrimitiveScalerConfig(this.min!!, this.max!!)

    override fun scale(dataPoint: Float): Float =
            if(this.min != null && this.max != null)
                (dataPoint - this.min!!) / (this.max!! - this.min!!)
            else throwUninitializedException()

    fun scale(dataPoint: Int): Float = scale(dataPoint.toFloat())

    private fun throwUninitializedException(): Nothing =
            throw IllegalStateException("You first have to initialize the scaler by either digesting a value or setting min and max directly")
}

data class PrimitiveScalerConfig(val min: Float, val max: Float)
