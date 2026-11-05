package com.example.hwitech_exam.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 臺灣證券交易所（TWSE）上市個股每日收盤價及月平均價資料模型。
 *
 * 用於解析 STOCK_DAY_AVG_ALL API 回傳的 JSON 資料。
 *
 * API: /exchangeReport/STOCK_DAY_AVG_ALL
 *
 * @property Date 資料日期
 * @property Code 股票代碼
 * @property Name 股票名稱
 * @property ClosingPrice 當日收盤價
 * @property MonthlyAveragePrice 當月平均收盤價
 */
data class StockDayAvgResponseDto(
    @SerializedName("Date")
    val date: String,

    @SerializedName("Code")
    val code: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("ClosingPrice")
    val closingPrice: String,

    @SerializedName("MonthlyAveragePrice")
    val monthlyAveragePrice: String
)
