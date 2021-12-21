package com.capstone.app.utrace_cts.vaxboosters.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaxbooster_table")
class VaxBoosterRecord constructor(
    @ColumnInfo(name = "vaxbrand")
    var vaxbrand: String,
    @ColumnInfo(name = "date")
    var date: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}