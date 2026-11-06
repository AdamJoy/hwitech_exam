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
 *
 * @property date 資料日期
 * @property code 股票代碼，例如：2330
 * @property name 股票名稱，例如：台積電
 * @property tradeVolume 成交股數
 * @property tradeValue 成交金額
 * @property openingPrice 開盤價
 * @property highestPrice 最高價
 * @property lowestPrice 最低價
 * @property closingPrice 收盤價
 * @property change 漲跌價差
 * @property transaction 成交筆數
 */
data class StockDayResponseDto(
    @SerializedName("Date")
    val date: String,

    @SerializedName("Code")
    val code: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("TradeVolume")
    val tradeVolume: String,

    @SerializedName("TradeValue")
    val tradeValue: String,

    @SerializedName("OpeningPrice")
    val openingPrice: String,

    @SerializedName("HighestPrice")
    val highestPrice: String,

    @SerializedName("LowestPrice")
    val lowestPrice: String,

    @SerializedName("ClosingPrice")
    val closingPrice: String,

    @SerializedName("Change")
    val change: String,

    @SerializedName("Transaction")
    val transaction: String,
)
