package com.example.hwitech_exam.data.mapper

import com.example.hwitech_exam.data.remote.dto.BwibbuResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayAvgResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayResponseDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * StockMapper 單元測試。
 *
 * 驗證 TWSE 三支 API 的資料是否能正確合併，
 * 並轉換成 Stock Domain Model。
 */
class StockMapperTest {

    private val gson = Gson()

    /**
     * 驗證：
     * 1. 三支 API 依股票代號正確合併。
     * 2. String 正確轉換為 Double / Long。
     * 3. 合併後的股票欄位內容正確。
     */
    @Test
    fun mergeStockData_shouldMergeThreeApiResponsesCorrectly() {

        // 準備 BWIBBU_ALL API測試資料
        val bwibbuDto = gson.fromJson(
            """
            {
                "Code": "2330",
                "Name": "台積電",
                "PEratio": "25.80",
                "DividendYield": "1.50",
                "PBratio": "7.20"
            }
            """.trimIndent(),
            BwibbuResponseDto::class.java
        )

        // 準備 STOCK_DAY_AVG_ALL API測試資料
        val avgDto = gson.fromJson(
            """
            {
                "Code": "2330",
                "Name": "台積電",
                "ClosingPrice": "1,500.00",
                "MonthlyAveragePrice": "1,480.50"
            }
            """.trimIndent(),
            StockDayAvgResponseDto::class.java
        )

        // 準備 STOCK_DAY_ALL API測試資料
        val dayDto = gson.fromJson(
            """
            {
                "Code": "2330",
                "Name": "台積電",
                "TradeVolume": "28,560,000",
                "TradeValue": "42,840,000,000",
                "OpeningPrice": "1,490.00",
                "HighestPrice": "1,510.00",
                "LowestPrice": "1,485.00",
                "ClosingPrice": "1,500.00",
                "Change": "+20.00",
                "Transaction": "35,000"
            }
            """.trimIndent(),
            StockDayResponseDto::class.java
        )

        // Act：執行要測試的方法，這邊測試 StockMapper() 合併資料
        val stocks = mergeStockData(
            bwibbuList = listOf(bwibbuDto),
            stockDayAvgList = listOf(avgDto),
            stockDayList = listOf(dayDto)
        )

        // 下方Assert驗證 如果實際值與預期值不同，JUnit 就會讓測試失敗。
        // Assert：驗證 - 確認只產生一筆股票資料
        assertEquals(1, stocks.size)

        val stock = stocks.first()

        // 驗證 - 股票基本資料
        assertEquals("2330", stock.code)
        assertEquals("台積電", stock.name)

        // BWIBBU_ALL
        assertEquals(25.80, stock.peRatio!!, 0.001)
        assertEquals(1.50, stock.dividendYield!!, 0.001)
        assertEquals(7.20, stock.pbRatio!!, 0.001)

        // STOCK_DAY_AVG_ALL
        assertEquals(1500.00, stock.closingPrice!!, 0.001)
        assertEquals(1480.50, stock.monthlyAveragePrice!!, 0.001)

        // STOCK_DAY_ALL
        assertEquals(1490.00, stock.openingPrice!!, 0.001)
        assertEquals(1510.00, stock.highestPrice!!, 0.001)
        assertEquals(1485.00, stock.lowestPrice!!, 0.001)
        assertEquals(20.00, stock.change!!, 0.001)

        assertEquals(28560000L, stock.tradeVolume)
        assertEquals(42840000000L, stock.tradeValue)
        assertEquals(35000L, stock.transaction)
    }
}