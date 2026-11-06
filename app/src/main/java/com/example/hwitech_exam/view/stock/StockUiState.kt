package com.example.hwitech_exam.view.stock

import com.example.hwitech_exam.domain.model.Stock

/**
 * 股票列表畫面的 UI 狀態。
 *
 * @property stocks 股票列表資料。
 * @property isLoading 是否正在初次載入資料。
 * @property isRefreshing 是否正在下拉刷新資料。
 * @property errorMessage 錯誤訊息，null 表示沒有錯誤。
 * @property sortOrder 目前股票排序方向。
 */
data class StockUiState(

    val stocks: List<Stock> = emptyList(),

    val isLoading: Boolean = false,

    val isRefreshing: Boolean = false,

    val errorMessage: String? = null,

    // 預設依股票代號降序排列
    val sortOrder: StockSortOrder = StockSortOrder.DESC
)
