package com.example.hwitech_exam.view.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwitech_exam.domain.repository.StockRepository

/**
 * StockViewModel Factory。
 *
 * 負責將 StockRepository 注入 StockViewModel。
 */
class StockViewModelFactory(
    private val repository: StockRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            return StockViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}