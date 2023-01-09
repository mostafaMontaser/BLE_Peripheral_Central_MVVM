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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swenson.blechat.model.signin.Account
import com.swenson.blechat.model.signin.User

@Entity(tableName = "account_table")
data class AccountTable(
    @PrimaryKey
    var accountId: String,

    @ColumnInfo(name = "token")
    var token: String
)
fun AccountTable.domainToModel(): Account {
    return Account(User(id = accountId), token)
}
