package com.example.hwitech_exam.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 上市個股每日成交資訊 DTO。
 *
 * API: GET /exchangeReport/STOCK_DAY_ALL
 *
 * 用於接收臺灣證券交易所（TWSE）回傳的
 * 上市股票每日成交資訊。
 *
 * 所有欄位皆維持 String 型別，
 * 後續由 Repository 或 Mapper 轉換為 Domain Model。
 */
data class StockDayResponseDto(

    /**
     * 資料日期
     */
    @SerializedName("Date")
    val date: String,

    /**
     * 股票代碼，例如：2330
     */
    @SerializedName("Code")
    val code: String,

    /**
     * 股票名稱，例如：台積電
     */
    @SerializedName("Name")
    val name: String,

    /**
     * 成交股數
     */
    @SerializedName("TradeVolume")
    val tradeVolume: String,

    /**
     * 成交金額
     */
    @SerializedName("TradeValue")
    val tradeValue: String,

    /**
     * 開盤價
     */
    @SerializedName("OpeningPrice")
    val openingPrice: String,

    /**
     * 最高價
     */
    @SerializedName("HighestPrice")
    val highestPrice: String,

    /**
     * 最低價
     */
    @SerializedName("LowestPrice")
    val lowestPrice: String,

    /**
     * 收盤價
     */
    @SerializedName("ClosingPrice")
    val closingPrice: String,

    /**
     * 漲跌價差
     */
    @SerializedName("Change")
    val change: String,

    /**
     * 成交筆數
     */
    @SerializedName("Transaction")
    val transaction: String,
)
