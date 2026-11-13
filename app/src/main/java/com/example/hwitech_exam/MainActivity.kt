package com.example.hwitech_exam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import android.app.AlertDialog
import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwitech_exam.data.remote.api.RetrofitClient
import com.example.hwitech_exam.data.repository.StockRepositoryImpl
import com.example.hwitech_exam.databinding.ActivityMainBinding
import com.example.hwitech_exam.databinding.BottomSheetStockSortBinding
import com.example.hwitech_exam.domain.model.Stock
import com.example.hwitech_exam.ui.theme.Hwitech_examTheme
import com.example.hwitech_exam.view.stock.StockAdapter
import com.example.hwitech_exam.view.stock.StockSortOrder
import com.example.hwitech_exam.view.stock.StockUiState
import com.example.hwitech_exam.view.stock.StockViewModel
import com.example.hwitech_exam.view.stock.StockViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: StockViewModel
    private val stockAdapter = StockAdapter { stock ->
        showStockDetailDialog(stock)
    }
    private var lastSortOrder: StockSortOrder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContent {
//            Hwitech_examTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }

        // 初始化 ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 避免畫面與系統 Status Bar、Navigation Bar 重疊
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // 建立 Repository
        val repository = StockRepositoryImpl(
            RetrofitClient.apiService
        )

        // 建立 ViewModelFactory
        val factory = StockViewModelFactory(repository)

        // 取得 ViewModel
        viewModel = ViewModelProvider(
            this,
            factory
        )[StockViewModel::class.java]

        // 初始化 RecyclerView
        setupRecyclerView()

        //測試： 設定成升序狀態
        //viewModel.setSortOrder(StockSortOrder.ASC)

        // 設定下拉刷新事件
        setupSwipeRefresh()
        // 設定排序按鈕事件
        setupSortButton()

        //測試：觀察 StateFlow
        observeUiState()

    }
    /**
     * 初始化股票 RecyclerView。
     */
    private fun setupRecyclerView() {

        val spanCount = when (
            resources.configuration.orientation
        ) {
            Configuration.ORIENTATION_LANDSCAPE -> 2
            else -> 1
        }

        binding.recyclerViewStocks.apply {
            layoutManager = GridLayoutManager(
                this@MainActivity,
                spanCount
            )
            adapter = stockAdapter
            // 避免排序時出現大量 Item 移動動畫
            itemAnimator = null
            setHasFixedSize(false)
        }
    }


    /**
     * 設定下拉刷新事件。
     */
    private fun setupSwipeRefresh() {

        binding.swipeRefreshLayout
            .setOnRefreshListener {
                Log.d(TAG, "使用者觸發下拉刷新")
                viewModel.refreshStocks()
            }
    }

    /**
     * 設定右上角排序按鈕事件。
     */
    private fun setupSortButton() {
        binding.btnSort.setOnClickListener {
            showSortBottomSheet()
        }
    }

    /**
     * 顯示股票排序 BottomSheet。
     *
     * 依據目前 ViewModel 的 sortOrder，
     * 自動勾選對應排序選項。
     */
    private fun showSortBottomSheet() {
        val sheetBinding = BottomSheetStockSortBinding.inflate(
            layoutInflater
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(sheetBinding.root)
        // 取得目前排序方向
        val currentOrder = viewModel.uiState.value.sortOrder

        when (currentOrder) {
            StockSortOrder.ASC ->
                sheetBinding.radioAscending.isChecked = true
            StockSortOrder.DESC ->
                sheetBinding.radioDescending.isChecked = true
        }

        sheetBinding.radioGroupSort
            .setOnCheckedChangeListener { _, checkedId ->
                val selectedOrder = when (checkedId) {
                    R.id.radioAscending -> StockSortOrder.ASC
                    R.id.radioDescending -> StockSortOrder.DESC
                    else -> return@setOnCheckedChangeListener
                }
                viewModel.setSortOrder(selectedOrder)
                dialog.dismiss()
            }

        // 顯示 BottomSheet
        dialog.show()

        // 橫向時展開 BottomSheet
        if (resources.configuration.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
        ) {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    /**
     * 觀察 ViewModel UI 狀態。
     */
    private fun observeUiState() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
        }
    }

    /**
     * 根據 StateFlow 更新畫面。
     */
    private fun renderUiState(state: StockUiState) {

        // 控制 SwipeRefreshLayout 刷新動畫
        binding.swipeRefreshLayout.isRefreshing =
            state.isRefreshing

        val sortChanged =
            lastSortOrder != null &&
                    lastSortOrder != state.sortOrder

        lastSortOrder = state.sortOrder

        stockAdapter.submitList(state.stocks) {
            if (sortChanged) {
                binding.recyclerViewStocks.scrollToPosition(0)
            }
        }

        // 顯示股票資料筆數
        binding.tvStatus.text =
            "股票數量：${state.stocks.size}"

        // 更新畫面狀態
        binding.tvStatus.text = when {
            state.isLoading ->
                "正在載入股票資料..."
            state.isRefreshing ->
                "正在重新取得股票資料..."
            state.errorMessage != null ->
                "資料取得失敗：${state.errorMessage}"
            else ->
                "資料取得完成，向下滑動可重新刷新"
        }

        // 只有載入完成後才印出股票資料
        if (!state.isLoading && !state.isRefreshing) {
            Log.d(TAG, "目前排序方向：${state.sortOrder}")
            Log.d(TAG, "股票總數：${state.stocks.size}")
            state.stocks.take(5).forEach { stock ->
                Log.d(TAG, "股票：${stock.code} ${stock.name}")
            }
        }
    }

    /**
     * 顯示股票詳細估值資訊。
     *
     * 包含：
     * 1. 本益比
     * 2. 殖利率
     * 3. 股價淨值比
     *
     * 資料直接使用已載入的 Stock，
     * 不需要重新呼叫 API。
     */
    private fun showStockDetailDialog(stock: Stock) {

        val message = """
        本益比：${stock.peRatio ?: "--"}
        殖利率：${stock.dividendYield?.let { "$it%" } ?: "--"}
        股價淨值比：${stock.pbRatio ?: "--"}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("${stock.name}（${stock.code}）")
            .setMessage(message)
            .setPositiveButton("關閉") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * 將數值格式化為小數點兩位。
     *
     * null → "--"
     * 25.8 → "25.80"
     */
    private fun formatDialogNumber(value: Double?): String {

        return value?.let {
            String.format(Locale.TAIWAN, "%,.2f", it)
        } ?: "--"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Hwitech_examTheme {
        Greeting("Android")
    }
}