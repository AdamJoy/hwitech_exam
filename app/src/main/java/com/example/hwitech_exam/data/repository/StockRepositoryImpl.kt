package com.example.hwitech_exam.data.repository

import com.example.hwitech_exam.data.mapper.mergeStockData
import com.example.hwitech_exam.data.mapper.toStockDouble
import com.example.hwitech_exam.data.mapper.toStockLong
import com.example.hwitech_exam.data.remote.TwseApiService
import com.example.hwitech_exam.domain.model.Stock
import com.example.hwitech_exam.domain.repository.StockRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * 股票資料 StockRepository 的實作。
 *
 * 負責透過 TWSE API 取得上市股票資料，
 * 並依股票代碼整合不同 API 的資料，
 * 最後轉換為 Stock Domain Model。
 *
 * 資料來源：
 * 1. BWIBBU_ALL：本益比、殖利率、股價淨值比。
 * 2. STOCK_DAY_AVG_ALL：收盤價、月平均收盤價。
 * 3. STOCK_DAY_ALL：每日成交資訊與股價資料。
 *
 * 使用 Kotlin Coroutines 平行請求三支 API，
 * 以減少整體資料取得時間。
 *
 * @property apiService TWSE Retrofit API 服務介面。
 */
class StockRepositoryImpl(
    private val apiService: TwseApiService
) : StockRepository {

    /**
     * 取得三支API資料並整合所有上市股票資料列表。
     *
     * 執行流程：
     * 1. 使用 async 平行呼叫三支 TWSE API。
     * 2. 使用 await 等待 API 回傳結果。
     * 3. 呼叫 mergeStockData() 整合 DTO 並轉換為 Stock Domain Model。
     *
     * @return 整合後的上市股票資料列表。
     */
    override suspend fun getStocks(): List<Stock> = coroutineScope {

        // 1. 平行取得三支 API 資料
        val bwibbuDeferred = async {
            apiService.getBwibbu()
        }

        val stockDayAvgDeferred = async {
            apiService.getStockDayAvg()
        }

        val stockDayDeferred = async {
            apiService.getStockDay()
        }

        // 2. 等待所有 API 回傳
        val bwibbuList = bwibbuDeferred.await()
        val stockDayAvgList = stockDayAvgDeferred.await()
        val stockDayList = stockDayDeferred.await()

        // 3. 整合 DTO 並轉換為 Domain Model
        mergeStockData(
            bwibbuList = bwibbuList,
            stockDayAvgList = stockDayAvgList,
            stockDayList = stockDayList
        )
    }
}