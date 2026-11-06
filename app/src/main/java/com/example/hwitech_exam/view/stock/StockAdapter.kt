package com.example.hwitech_exam.view.stock

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hwitech_exam.databinding.ItemStockBinding
import com.example.hwitech_exam.domain.model.Stock

/**
 * 股票列表 Adapter。
 *
 * 使用：
 * 1. RecyclerView 顯示列表。
 * 2. ListAdapter 管理資料。
 * 3. DiffUtil 比較新舊股票。
 * 4. ComposeView 顯示 StockCard。
 */
class StockAdapter(
    private val onStockClick: (Stock) -> Unit
) : ListAdapter<Stock, StockAdapter.StockViewHolder>(
    StockDiffCallback()
){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockViewHolder {

        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StockViewHolder(
            binding = binding,
            onStockClick = onStockClick)
    }

    override fun onBindViewHolder(
        holder: StockViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    /**
     * 股票 ViewHolder。
     *
     * ComposeView 只設定一次 Composable 內容，
     * 後續透過 Compose State 更新股票資料。
     */
    class StockViewHolder(
        binding: ItemStockBinding,
        private val onStockClick: (Stock) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var stockState by mutableStateOf<Stock?>(null)

        init {
            binding.composeView.setContent {
                MaterialTheme {
                    stockState?.let { stock ->
                        StockCard(
                            stock = stock,
                            onClick = {
                                // 通知 Adapter 外部的點擊事件
                                onStockClick(stock)
                            }
                        )
                    }
                }
            }
        }
        /**
         * 綁定股票資料。
         */
        fun bind(stock: Stock) {
            stockState = stock
        }
    }
}

    /**
     * 比較 RecyclerView 的新舊資料。
     */
    private class StockDiffCallback :
        DiffUtil.ItemCallback<Stock>() {

        /**
         * 是否為相同股票。
         *
         * 股票代號作為唯一識別。
         */
        override fun areItemsTheSame(
            oldItem: Stock,
            newItem: Stock
        ): Boolean {
            return oldItem.code == newItem.code
        }

        /**
         * 股票資料內容是否相同。
         */
        override fun areContentsTheSame(
            oldItem: Stock,
            newItem: Stock
        ): Boolean {
            return oldItem == newItem
        }

}