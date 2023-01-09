/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swenson.blechat.datasource.room.account

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDatabaseDao {

    @Insert
    suspend fun insert(account: AccountTable?)

    @Update
    suspend fun update(account: AccountTable)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from account_table WHERE accountId = :key")
    suspend fun get(key: Long): AccountTable?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM account_table")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM account_table ORDER BY accountId DESC")
    fun getAllAccounts(): LiveData<List<AccountTable>>

    /**
     * Selects and returns the latest night.
     */
    @Query("SELECT * FROM account_table ORDER BY accountId DESC LIMIT 1")
    suspend fun getAccount(): AccountTable?

}

