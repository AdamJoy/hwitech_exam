package com.example.hwitech_exam.ui.stock

import com.example.hwitech_exam.domain.model.Stock
import com.example.hwitech_exam.domain.repository.StockRepository
import com.example.hwitech_exam.view.stock.StockSortOrder
import com.example.hwitech_exam.view.stock.StockViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * StockViewModel 單元測試。
 *
 * 驗證：
 * 1. 預設股票排序為 DESC。
 * 2. 切換 ASC 時股票正確排序。
 * 3. 切換 DESC 時股票正確排序。
 * 4. 切換排序不重新呼叫 Repository。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * 驗證股票列表排序及 Repository 呼叫次數。
     */
    @Test
    fun sortStocks_shouldUpdateOrderWithoutReloadingData() = runTest {

        // Arrange：建立假的 Repository
        val repository = FakeStockRepository()

        // 建立 ViewModel，init 會呼叫 loadStocks()
        val viewModel = StockViewModel(repository)

        // 執行目前排程中的 Coroutine
        advanceUntilIdle()

        // Assert：驗證初次載入與預設降序
        assertEquals(
            StockSortOrder.DESC,
            viewModel.uiState.value.sortOrder
        )

        assertEquals(
            listOf("2330", "2317", "1101"),
            viewModel.uiState.value.stocks.map { it.code }
        )

        assertEquals(1, repository.callCount)

        // Act：切換升序
        viewModel.setSortOrder(StockSortOrder.ASC)

        // Assert：驗證升序
        assertEquals(
            StockSortOrder.ASC,
            viewModel.uiState.value.sortOrder
        )

        assertEquals(
            listOf("1101", "2317", "2330"),
            viewModel.uiState.value.stocks.map { it.code }
        )

        // Act：執行要測試的方法，這邊測試 setSortOrder() 切換降序
        viewModel.setSortOrder(StockSortOrder.DESC)

        // Assert：驗證降序
        assertEquals(
            listOf("2330", "2317", "1101"),
            viewModel.uiState.value.stocks.map { it.code }
        )

        // Assert：確認沒有重新呼叫 Repository
        assertEquals(1, repository.callCount)

        // Assert：確認載入完成且沒有錯誤
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertNull(viewModel.uiState.value.errorMessage)
    }


    /**
     * 測試用 Fake Repository。
     *
     * 提供固定的股票資料，
     * 驗證 ViewModel 排序及 Repository 呼叫次數。
     */
    private class FakeStockRepository : StockRepository {

        var callCount = 0
            private set

        override suspend fun getStocks(): List<Stock> {

            callCount++

            return listOf(
                createStock("2330", "台積電"),
                createStock("1101", "台泥"),
                createStock("2317", "鴻海")
            )
        }

        /**
         * 建立測試用股票資料。
         *
         * 本次只測試 code 和 name，
         * 其他非必要欄位設定為 null。
         */
        private fun createStock(
            code: String,
            name: String
        ): Stock {

            return Stock(
                code = code,
                name = name,

                // BWIBBU_ALL
                peRatio = null,
                dividendYield = null,
                pbRatio = null,

                // STOCK_DAY_AVG_ALL
                closingPrice = null,
                monthlyAveragePrice = null,

                // STOCK_DAY_ALL
                date = null,
                tradeVolume = null,
                tradeValue = null,
                openingPrice = null,
                highestPrice = null,
                lowestPrice = null,
                change = null,
                transaction = null
            )
        }
    }

}

/**
 * 將 Dispatchers.Main 替換成測試 Dispatcher。
 *
 * 每次測試開始前設定，
 * 測試結束後恢復原本的 Main Dispatcher。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
