package com.geotask.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val locationId: Long? = null,
    val deadline: Long? = null,
    val description: String? = null,
    val status: Status = Status.ACTIVE
) {

    class Builder(private val title: String) {

        private var locationId: Long? = null
        private var deadline: Long? = null
        private var description: String? = null
        private var status: Status = Status.ACTIVE

        fun locationId(id: Long?) = apply { this.locationId = id }
        fun deadline(deadline: Long?) = apply { this.deadline = deadline }
        fun description(desc: String?) = apply { this.description = desc?.trim() }
        fun status(status: Status) = apply { this.status = status }

        fun build(): Task {
            require(title.isNotBlank()) { "Task title cannot be empty" }

            return Task(
                title = title.trim(),
                locationId = locationId,
                deadline = deadline,
                description = description,
                status = status
            )
        }
    }
}

enum class Status { ACTIVE, COMPLETED, DEFERRED }