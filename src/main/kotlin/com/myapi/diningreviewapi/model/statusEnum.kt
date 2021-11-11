package com.myapi.diningreviewapi.model

enum class StatusEnum(val status: Byte) {
    pending(0),
    approved(1),
    dismissed(-1)
}