package io.rightpad.daxplorer.data.scalers

import junit.framework.Assert.*
import org.junit.Test
import java.lang.IllegalStateException

private const val EQUALITY_DELTA = .00001f

class PrimitiveScalerTest {

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
}