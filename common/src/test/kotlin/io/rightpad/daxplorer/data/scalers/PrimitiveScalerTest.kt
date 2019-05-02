package io.rightpad.daxplorer.data.scalers

import junit.framework.Assert.*
import org.junit.Test
import java.lang.IllegalStateException

private const val EQUALITY_DELTA = .00001f

class PrimitiveScalerTest {

    @Test
    fun testIngestSingleValue() {
        val scaler = PrimitiveScaler()

        val singleValue = 100f
        scaler.ingest(singleValue)

        assertMinMax(scaler, singleValue, singleValue)
    }

    @Test
    fun testIngestMinAndMaxValue() {
        val scaler = PrimitiveScaler()

        val minValue = 10f
        val maxValue = 100f
        scaler.ingest(minValue)
        scaler.ingest(maxValue)

        assertMinMax(scaler, minValue, maxValue)
    }

    @Test
    fun testIngestMultipleValues() {
        val minValue = 10f
        val maxValue = 100f

        val scaler = PrimitiveScaler(minValue, maxValue)
        ingestRandomNumbersBetween(maxValue, minValue, scaler)

        assertMinMax(scaler, minValue, maxValue)
    }

    @Test
    fun testIngestInitialized() {
        val scaler = PrimitiveScaler()

        val minValue = 10f
        val maxValue = 100f
        scaler.ingest(minValue)
        scaler.ingest(maxValue)

        ingestRandomNumbersBetween(maxValue, minValue, scaler)

        assertMinMax(scaler, minValue, maxValue)
    }

    private fun assertMinMax(scaler: PrimitiveScaler, minValue: Float, maxValue: Float) {
        assertNotNull(scaler.min)
        assertNotNull(scaler.max)
        assertEquals(minValue, scaler.min!!, EQUALITY_DELTA)
        assertEquals(maxValue, scaler.max!!, EQUALITY_DELTA)
    }

    private fun ingestRandomNumbersBetween(maxValue: Float, minValue: Float, scaler: PrimitiveScaler) {
        val dynamicRange = maxValue - minValue
        val otherValues = Array(1000) { Math.random().toFloat() * dynamicRange + minValue }
        otherValues.forEach { scaler.ingest(it) }
    }

    @Test
    fun testScale() {
        val scaler = PrimitiveScaler(10f, 20f)
        val scaled = scaler.scale(15)
        assertEquals(.5f, scaled, EQUALITY_DELTA)
    }

    @Test
    fun testScaleInvalidState() {
        val scaler = PrimitiveScaler()
        try {
            scaler.scale(15)
        }
        catch(exc: IllegalStateException) {
            return
        }
        fail("No exception was thrown")
    }

    @Test
    fun testScaleMultipleFromZero() {
        val scaler = PrimitiveScaler(0f, 20f)
        assertEquals(.1f, scaler.scale(2), EQUALITY_DELTA)
        assertEquals(.25f, scaler.scale(5), EQUALITY_DELTA)
        assertEquals(.5f, scaler.scale(10), EQUALITY_DELTA)
        assertEquals(.75f, scaler.scale(15), EQUALITY_DELTA)
    }

    @Test
    fun testScaleMultiple() {
        val scaler = PrimitiveScaler(5f, 20f)
        assertEquals(.5f, scaler.scale(5 + 7.5f), EQUALITY_DELTA)
        assertEquals(.75f, scaler.scale(5 + 7.5f + 3.75f), EQUALITY_DELTA)
    }

    @Test
    fun testIngestAndScale() {
        val scaler = PrimitiveScaler()

        val minValue = 250f
        val maxValue = 750f
        scaler.ingest(minValue)
        scaler.ingest(maxValue)

        ingestRandomNumbersBetween(minValue, maxValue, scaler)

        assertEquals(.5f, scaler.scale(500), EQUALITY_DELTA)
        assertEquals(1f, scaler.scale(750), EQUALITY_DELTA)
    }
}