package com.carles.lalloriguera.data

import com.carles.lalloriguera.data.local.TaskEntity
import com.carles.lalloriguera.data.remote.TaskRef
import com.carles.lalloriguera.model.Tasc
import javax.inject.Inject

class TaskMapper @Inject constructor() {

    fun fromEntity(entity: TaskEntity): Tasc = with(entity) {
        Tasc(id?.toString(), name, isOneTime, lastDone, periodicity, notificationsOn)
    }

    fun toEntity(model: Tasc): TaskEntity = with(model) {
        TaskEntity(id?.toInt(), name, isOneTime, lastDate, periodicity, notificationsOn)
    }

    fun fromRef(ref: TaskRef): Tasc = with(ref) {
        Tasc(id, name ?: "", oneTime ?: false, lastDone ?: 0L, periodicity ?: 0, notificationsOn ?: false)
    }

    fun toRef(model: Tasc): TaskRef = with(model) {
        TaskRef(id, name, isOneTime, lastDate, periodicity, notificationsOn)
    }
}