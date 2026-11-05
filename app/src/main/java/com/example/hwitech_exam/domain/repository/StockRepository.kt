package com.example.hwitech_exam.domain.repository

import com.example.hwitech_exam.domain.model.Stock

/**
 * StockRepository
 *
 * 股票資料存取介面（Repository Interface）。
 *
 * 主要職責：
 * 1. 定義取得上市股票資料的方法。
 * 2. 隱藏底層 API 與資料來源的實作細節。
 * 3. 提供 ViewModel 或 UseCase 統一的資料存取入口。
 * 4. 降低 UI Layer 與 Data Layer 的耦合。
 *
 * 實際資料取得與合併邏輯，
 * 由 Data Layer 的 StockRepositoryImpl 負責實作。
 */
interface StockRepository {

    /**
     * 取得上市股票列表。
     *
     * 從臺灣證券交易所 OpenAPI 取得三組資料：
     *
     * 1. 本益比、殖利率、股價淨值比
     * 2. 收盤價、月平均價
     * 3. 開盤價、最高價、最低價、收盤價、
     *    漲跌價差、成交股數、成交金額、成交筆數
     *
     * 由 StockRepositoryImpl 依股票代號 Code
     * 合併為統一的 Stock Domain Model。
     *
     * @return List<Stock> 合併後的上市股票資料。
     *
     * @throws Exception API 請求或資料處理失敗時，
     *                   由呼叫端負責處理例外。
     */
    suspend fun getStocks(): List<Stock>

}