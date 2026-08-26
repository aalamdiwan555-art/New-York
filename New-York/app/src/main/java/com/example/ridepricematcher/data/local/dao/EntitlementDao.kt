package com.example.ridepricematcher.data.local.dao

import androidx.room.*
import com.example.ridepricematcher.data.local.entity.CachedEntitlementEntity

@Dao
interface EntitlementDao {
    @Query("SELECT * FROM entitlements WHERE userId = :userId")
    suspend fun getEntitlement(userId: String): CachedEntitlementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entitlement: CachedEntitlementEntity)

    @Query("DELETE FROM entitlements WHERE userId = :userId")
    suspend fun delete(userId: String)
}
