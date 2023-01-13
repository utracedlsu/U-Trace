package com.capstone.app.utrace_cts.fragments

import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecord
import com.capstone.app.utrace_cts.status.persistence.StatusRecord
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecord

data class ExportData(val recordList: List<StreetPassRecord>, val statusList: List<StatusRecord>)

data class NotifData(val notifList: List<NotificationRecord>)