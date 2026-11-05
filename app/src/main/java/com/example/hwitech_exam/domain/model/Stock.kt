package com.example.hwitech_exam.domain.model

/**
 * 股票 Domain Model。
 *
 * 整合 TWSE 三支 API 的股票資料。
 *
 * 此 Model 不直接依賴 Retrofit 或 Gson，
 * 提供給 Domain、ViewModel 及 UI 層使用。
 */
data class Stock(

    // 股票基本資料
    val code: String,
    val name: String,

    // BWIBBU_ALL
    val peRatio: Double?,
    val dividendYield: Double?,
    val pbRatio: Double?,

    // STOCK_DAY_AVG_ALL
    val closingPrice: Double?,
    val monthlyAveragePrice: Double?,

    // STOCK_DAY_ALL
    val date: String? = null,
    val tradeVolume: Long?,
    val tradeValue: Long?,
    val openingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    val change: Double?,
    val transaction: Long?
)