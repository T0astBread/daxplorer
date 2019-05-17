package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import org.junit.Test
import java.time.LocalDateTime

class MACDTest {
    @Test
    fun testMACD() {
        val averageFeature2: AverageFeature = AverageFeature(10)
        val averageFeature1: AverageFeature = AverageFeature(7)

        val someIndexDataPoints = mutableListOf<IndexDataPoint>()
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-10T23:55:00"), 5329f, 5321f, 5205f, 5421f, 291271, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-11T23:55:00"), 5350f, 5122f, 5098.5f, 5421.5f, 301256, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-12T23:55:00"), 5125f, 5321f, 5123f, 5326f, 283652, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-13T23:55:00"), 5290f, 5187f, 5026f, 5295f, 458795, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-14T23:55:00"), 5190f, 5180f, 5120f, 5254f, 396585, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-15T23:55:00"), 5187f, 5212f, 5124f, 5214f, 369858, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-16T23:55:00"), 5201f, 5101f, 5189f, 5365f, 245879, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-17T23:55:00"), 5080f, 5087f, 5047f, 5180f, 295685, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-18T23:55:00"), 5098f, 5091f, 5032f, 5170f, 312654, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-19T23:55:00"), 5023f, 5018f, 4987f, 5123f, 358975, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-20T23:55:00"), 5017f, 5004f, 4934f, 5100f, 378982, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-21T23:55:00"), 5000f, 4971f, 4945f, 5040f, 358254, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-22T23:55:00"), 4976f, 4984f, 4943f, 5012f, 353254, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-23T23:55:00"), 4987f, 5001f, 4967f, 5042f, 396541, 0))
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-24T23:55:00"), 5003f, 5018f, 4976f, 5065f, 312543, 0))

        val temp = MACD(averageFeature1, averageFeature2)
        temp.calculate(someIndexDataPoints)
        val temp2: Int = 0
        someIndexDataPoints.add(IndexDataPoint(LocalDateTime.parse("2018-11-10T23:55:00"), 5329f, 5321f, 5205f, 5421f, 291271, 0))
    }
}
