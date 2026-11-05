package com.example.hwitech_exam.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 臺灣證券交易所（TWSE）上市個股本益比、殖利率及股價淨值比資料模型。
 *
 * 用於解析 BWIBBU_ALL API 回傳的 JSON 資料。
 *
 * API: /exchangeReport/BWIBBU_ALL
 *
 * @property Date 資料日期
 * @property Code 股票代碼
 * @property Name 股票名稱
 * @property PEratio 本益比（P/E Ratio）
 * @property DividendYield 殖利率（Dividend Yield）
 * @property PBratio 股價淨值比（P/B Ratio）
 */
data class BwibbuResponseDto(
    @SerializedName("Date")
    val date: String,

    @SerializedName("Code")
    val code: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("PEratio")
    val peRatio: String,

    @SerializedName("DividendYield")
    val dividendYield: String,

    @SerializedName("PBratio")
    val pbRatio: String
)
