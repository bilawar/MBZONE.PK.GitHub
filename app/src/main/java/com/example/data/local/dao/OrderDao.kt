package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE consignmentNumber = :cn LIMIT 1")
    fun getOrderByCn(cn: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE orderStatus = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    @Query("SELECT SUM(grandTotal) FROM orders WHERE orderStatus != 'CANCELLED'")
    fun getTotalRevenue(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)
}
