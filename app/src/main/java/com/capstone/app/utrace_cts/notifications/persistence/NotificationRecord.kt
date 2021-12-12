package com.capstone.app.utrace_cts.notifications.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table")
class NotificationRecord constructor(
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "body")
    var body: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "isread")
    var isread: Boolean = false

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()
}