package com.example.hwitech_exam.view.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hwitech_exam.domain.model.Stock
import java.util.Locale

/**
 * 股票資訊卡片
 *
 * 顯示：
 * 1. 股票名稱、股票代號
 * 2. 收盤價、漲跌價差
 * 3. 股價資訊（每列兩個欄位）
 * 4. 成交資訊
 *
 * 點擊 Card 時，透過 onClick 顯示股票詳細資訊。
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
        stock.closingPrice > stock.monthlyAveragePrice -> redColor
        stock.closingPrice < stock.monthlyAveragePrice -> greenColor
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
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ==========================
            // 股票名稱、代號、收盤價
            // ==========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stock.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stock.code,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatPrice(stock.closingPrice),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = closingPriceColor
                    )
                    Text(
                        text = formatChange(stock.change),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = changeColor
                    )
                }
            }

            HorizontalDivider()

            // ==========================
            // 股價資訊：每列兩個欄位
            // ==========================
            Text(
                text = "股價資訊",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            // 第一列：開盤價、收盤價
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockPriceItem(
                    label = "開盤價",
                    value = formatPrice(stock.openingPrice),
                    modifier = Modifier.weight(1f)
                )
                StockPriceItem(
                    label = "收盤價",
                    value = formatPrice(stock.closingPrice),
                    valueColor = closingPriceColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // 第二列：最高價、最低價
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockPriceItem(
                    label = "最高價",
                    value = formatPrice(stock.highestPrice),
                    modifier = Modifier.weight(1f)
                )
                StockPriceItem(
                    label = "最低價",
                    value = formatPrice(stock.lowestPrice),
                    modifier = Modifier.weight(1f)
                )
            }

            // 第三列：月平均價
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockPriceItem(
                    label = "月平均價",
                    value = formatPrice(stock.monthlyAveragePrice),
                    modifier = Modifier.weight(1f)
                )
                // 保留右側空間，維持兩欄排版
                StockPriceItem(
                    label = "",
                    value = "",
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            // ==========================
            // 成交資訊
            // ==========================
            Text(
                text = "成交資訊",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            StockInfoRow(
                label = "成交筆數",
                value = "${formatLong(stock.transaction)} (筆)"
            )
            StockInfoRow(
                label = "成交股數",
                value = "${formatLong(stock.tradeVolume)} (股)"
            )
            StockInfoRow(
                label = "成交金額",
                value = "${formatLong(stock.tradeValue)} (元)"
            )
        }
    }
}

/**
 * 股價資訊欄位。
 *
 * Title 在左邊，數值在右邊。
 */
@Composable
private fun StockPriceItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/**
 * 成交資訊欄位
 *
 * 左側顯示名稱，右側顯示數值。
 */
@Composable
private fun StockInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

/**
 * 股價格式化
 *
 * null -> --
 * 1500.5 -> 1,500.50
 */
private fun formatPrice(value: Double?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%,.2f", it)
    } ?: "--"
}

/**
 * 漲跌價差格式化
 *
 * 20.0 -> +20.00
 * -3.5 -> -3.50
 * null -> --
 */
private fun formatChange(value: Double?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%+,.2f", it)
    } ?: "--"
}

/**
 * 整數格式化
 *
 * 28560000 -> 28,560,000
 * null -> --
 */
private fun formatLong(value: Long?): String {
    return value?.let {
        String.format(Locale.TAIWAN, "%,d", it)
    } ?: "--"
}
