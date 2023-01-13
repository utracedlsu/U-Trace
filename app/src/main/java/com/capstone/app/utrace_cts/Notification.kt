package com.capstone.app.utrace_cts

data class Notification (
    var id: Int,
    var type: String,
    var date: String,
    var time: String,
    var header: String,
    var content: String
    ) {
}