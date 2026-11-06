package com.example.hwitech_exam.view.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hwitech_exam.domain.model.Stock
import java.util.Locale

/**
 * 股票資訊卡片。
 *
 * @param stock 股票 Domain Model。
 *
 * 顏色規則：
 * 1. 收盤價高於月平均價：紅色。
 * 2. 收盤價低於月平均價：綠色。
 * 3. 漲跌價差正數：紅色。
 * 4. 漲跌價差負數：綠色。
 */
@Composable
fun StockCard(
    stock: Stock,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val redColor = Color(0xFFD32F2F)
    val greenColor = Color(0xFF388E3C)
    val normalColor = MaterialTheme.colorScheme.onSurface

    // 收盤價與月平均價比較
    val closingPriceColor = when {
        stock.closingPrice == null ||
                stock.monthlyAveragePrice == null -> normalColor

        stock.closingPrice > stock.monthlyAveragePrice ->
            redColor

        stock.closingPrice < stock.monthlyAveragePrice ->
            greenColor

        else -> normalColor
    }

    // 漲跌價差顏色
    val changeColor = when {
        stock.change == null -> normalColor
        stock.change > 0 -> redColor
        stock.change < 0 -> greenColor
        else -> normalColor
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ).clickable{
                onClick()   // 將事件傳出去
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 股票名稱、代號及收盤價
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = stock.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stock.code,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment =
                        androidx.compose.ui.Alignment.End
                ) {

                    Text(
                        text = formatPrice(stock.closingPrice),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = closingPriceColor
                    )

                    Text(
                        text = formatChange(stock.change),
                        fontSize = 14.sp,
                        color = changeColor
                    )
                }
            }

            HorizontalDivider()

            // 月平均價與成交股數
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "月平均價",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = formatPrice(stock.monthlyAveragePrice),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment =
                        androidx.compose.ui.Alignment.End
                ) {

                    Text(
                        text = "成交股數",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = formatVolume(stock.tradeVolume),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 股價格式化。
 *
 * null -> "--"
 * 1500.5 -> "1,500.50"
 */
private fun formatPrice(value: Double?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%,.2f", it)
    } ?: "--"
}

/**
 * 漲跌價差格式化。
 *
 * 正數加上 "+"，負數保留 "-"。
 */
private fun formatChange(value: Double?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%+,.2f", it)
    } ?: "--"
}

/**
 * 成交股數格式化。
 */
private fun formatVolume(value: Long?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%,d", it)
    } ?: "--"
}
