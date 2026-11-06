package com.example.hwitech_exam.view.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwitech_exam.domain.model.Stock
import com.example.hwitech_exam.domain.repository.StockRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 股票列表 ViewModel。
 *
 * 負責：
 * 1. 透過 Repository 取得上市股票資料。
 * 2. 使用 StateFlow 管理股票列表 UI 狀態。
 * 3. 管理初次載入的 Loading 狀態。
 * 4. 處理資料取得失敗的情況。
 *
 * @property stockRepository 股票資料存取介面。
 */
class StockViewModel(
    private val stockRepository: StockRepository
) : ViewModel() {

    /**
     * ViewModel 內部可修改的 UI State。
     */
    private val _uiState = MutableStateFlow(
        StockUiState()
    )

    /**
     * 提供給 View 觀察的唯讀 UI State。
     */
    val uiState: StateFlow<StockUiState> =
        _uiState.asStateFlow()

    // 追蹤目前執行中的載入工作
    private var loadJob: Job? = null

    /**
     * ViewModel 建立後，載入股票資料。
     */
    init {
        loadStocks()
    }

    /**
     * 初次載入股票資料。
     */
    fun loadStocks() {
        fetchStocks(isRefresh = false)
    }

    /**
     * 下拉刷新股票資料。
     * 保留舊資料，重新呼叫 Repository。
     */
    fun refreshStocks() {
        fetchStocks(isRefresh = true)
    }

    /**
     * 設定股票排序方向。
     *
     * 只重新排序目前的股票列表，
     * 不重新呼叫 API。
     *
     * @param order ASC 升序、DESC 降序。
     */
    fun setSortOrder(order: StockSortOrder) {

        _uiState.update { currentState ->
            // 排序方向沒有變化就不必重新排序
            if (currentState.sortOrder == order) {
                return@update currentState
            }
            currentState.copy(
                sortOrder = order,
                stocks = sortStocks(
                    currentState.stocks,
                    order
                )
            )
        }
    }

    /**
     * 根據股票代號排序。
     *
     * 股票代號為 String，
     * 可能包含英文字母及前導零。
     */
    private fun sortStocks(
        stocks: List<Stock>,
        order: StockSortOrder
    ): List<Stock> {
        return when (order) {
            StockSortOrder.ASC ->
                stocks.sortedBy { it.code }
            StockSortOrder.DESC ->
                stocks.sortedByDescending { it.code }
        }
    }

    /**
     * 統一處理股票資料載入與刷新。
     *
     * @param isRefresh true：下拉刷新
     *                  false：一般載入
     */
    private fun fetchStocks(isRefresh: Boolean) {

        // 避免同時執行多次 API 請求
        if (loadJob?.isActive == true) {
            return
        }

        loadJob = viewModelScope.launch {

            // 開始載入
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            try {
                // 重新向 Repository 取得股票資料
                val stocks = stockRepository.getStocks()

                // 根據目前排序方向更新股票資料(根據排序設定)
                _uiState.update { currentState ->

                    currentState.copy(
                        stocks = sortStocks(
                            stocks,
                            currentState.sortOrder
                        ),
                        errorMessage = null
                    )
                }

            } catch (e: CancellationException) {
                // 保留 Coroutine 取消語意
                throw e
            } catch (e: Exception) {
                // API 失敗時不清空舊股票資料
                _uiState.update {
                    it.copy(
                        errorMessage =
                            e.message ?: "股票資料取得失敗"
                    )
                }
            } finally {
                // 無論成功或失敗，都關閉載入狀態
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }

}