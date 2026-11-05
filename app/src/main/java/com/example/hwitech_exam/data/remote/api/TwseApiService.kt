package com.example.hwitech_exam.data.remote

import com.example.hwitech_exam.data.remote.dto.BwibbuResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayAvgResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayResponseDto
import retrofit2.http.GET

/**
 * 臺灣證券交易所（TWSE）API 服務介面。
 *
 * 使用 Retrofit 定義股票相關資料的 API 請求，
 * 包含本益比、殖利率、股價淨值比、收盤價及每日成交資訊。
 *
 * Base URL: https://openapi.twse.com.tw/v1/
 *
 * 各 API 回傳 JSON Array，
 * 並透過 Gson 轉換為對應的 DTO List。
 */
interface TwseApiService {

    /**
     * 取得上市個股日本益比、殖利率及股價淨值比。
     * @return 上市股票的本益比、殖利率及股價淨值比資料列表。
     */
    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbu(): List<BwibbuResponseDto>

    /**
     * 取得上市個股日收盤價及月平均價。
     * @return 上市股票的收盤價及月平均價資料列表。
     */
    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvg(): List<StockDayAvgResponseDto>

    /**
     * 取得上市個股日成交資訊。
     * @return 上市股票的每日成交資訊列表。
     */
    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDay(): List<StockDayResponseDto>

}