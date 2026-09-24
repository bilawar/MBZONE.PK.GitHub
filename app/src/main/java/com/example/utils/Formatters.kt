package com.example.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val pkrFormat = NumberFormat.getIntegerInstance(Locale("en", "PK"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
    private val shortDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)

    /**
     * Format number to "Rs. 1,450"
     */
    fun formatPkr(amount: Long): String {
        return "Rs. ${pkrFormat.format(amount)}"
    }

    fun formatPkr(amount: Double): String {
        return "Rs. ${pkrFormat.format(amount.toLong())}"
    }

    /**
     * Format timestamp to human readable date string
     */
    fun formatDate(timestampMillis: Long): String {
        return dateFormat.format(Date(timestampMillis))
    }

    fun formatShortDate(timestampMillis: Long): String {
        return shortDateFormat.format(Date(timestampMillis))
    }
}
