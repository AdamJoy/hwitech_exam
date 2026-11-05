package com.example.hwitech_exam.data.mapper

import com.example.hwitech_exam.data.remote.dto.BwibbuResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayAvgResponseDto
import com.example.hwitech_exam.data.remote.dto.StockDayResponseDto
import com.example.hwitech_exam.domain.model.Stock
import com.example.hwitech_exam.data.mapper.toStockLong

/**
 * StockMapper
 *
 * 負責將 TWSE 三支 API 回傳的 DTO，
 * 依股票代號 Code 合併成 Stock Domain Model。
 *
 * 主要功能：
 * 1. 依股票代號合併三組 API 資料。
 * 2. 將字串數值轉換成 Double? / Long?。
 * 3. 處理空字串、"--"、逗號及無效數值。
 * 4. 不依賴 Android Framework。
 */

/**
 * 將三支 API 的資料合併成股票列表。
 *
 * 以 STOCK_DAY_ALL 作為主要資料來源，
 * 再補上 STOCK_DAY_AVG_ALL 和 BWIBBU_ALL 的資訊。
 *
 * @return List<Stock> 整合後的股票列表。
 */
fun mergeStockData(
    bwibbuList: List<BwibbuResponseDto>,
    stockDayAvgList: List<StockDayAvgResponseDto>,
    stockDayList: List<StockDayResponseDto>
): List<Stock> {

    // 依股票代號建立 Map，提升查找效率
    val bwibbuMap = bwibbuList.associateBy {
        it.code.trim()
    }

    val stockDayAvgMap = stockDayAvgList.associateBy {
        it.code.trim()
    }

    // 以每日成交資訊為主要列表
    return stockDayList
        .filter { it.code.isNotBlank() }
        .distinctBy { it.code.trim() }
        .map { dayDto ->

            val code = dayDto.code.trim()

            // 尋找相同股票代號的其他 API 資料
            val bwibbuDto = bwibbuMap[code]
            val avgDto = stockDayAvgMap[code]

            Stock(
                // 股票基本資料
                code = code,
                name = dayDto.name.trim(),

                // ==========================
                // BWIBBU_ALL
                // ==========================
                peRatio = bwibbuDto?.peRatio.toStockDouble(),
                dividendYield = bwibbuDto?.dividendYield.toStockDouble(),
                pbRatio = bwibbuDto?.pbRatio.toStockDouble(),

                // ==========================
                // STOCK_DAY_AVG_ALL
                // ==========================
                closingPrice = (
                        avgDto?.closingPrice.toStockDouble()
                            ?: dayDto.closingPrice.toStockDouble()
                        ),

                monthlyAveragePrice =
                    avgDto?.monthlyAveragePrice.toStockDouble(),

                // ==========================
                // STOCK_DAY_ALL
                // ==========================
                date = dayDto?.date,
                openingPrice = dayDto.openingPrice.toStockDouble(),
                highestPrice = dayDto.highestPrice.toStockDouble(),
                lowestPrice = dayDto.lowestPrice.toStockDouble(),
                change = dayDto.change.toStockDouble(),

                tradeVolume = dayDto.tradeVolume.toStockLong(),
                tradeValue = dayDto.tradeValue.toStockLong(),
                transaction = dayDto.transaction.toStockLong()
            )
        }
}

/**
 * 將 TWSE API 的字串數值轉成 Double?。
 *
 * 範例：
 * "1,250.50" -> 1250.5
 * "3.45"     -> 3.45
 * "--"       -> null
 * ""         -> null
 * null       -> null
 */
fun String?.toStockDouble(): Double? {
    return this
        ?.trim()
        ?.replace(",", "")
        ?.takeIf { it.isNotBlank() }
        ?.toDoubleOrNull()
        ?.takeIf { it.isFinite() }
}

/**
 * 將 TWSE API 的字串數值轉成 Long?。
 *
 * 範例：
 * "12,500,000" -> 12500000
 * "500"        -> 500
 * "--"         -> null
 * ""           -> null
 * null         -> null
 */
fun String?.toStockLong(): Long? {
    return this
        ?.trim()
        ?.replace(",", "")
        ?.takeIf { it.isNotBlank() }
        ?.toLongOrNull()
}