package com.myapi.diningreviewapi.model

import java.sql.Timestamp
import java.util.*
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "TOKENS")
@Entity
open class Token {

    @Column(name = "USER_ID", nullable = false)
    open var userID: Long? = null

    @Id
    @Column(name = "UUID_STRING", unique = true)
    open val uuidString: String =  UUID.randomUUID().toString()

    @Column(name = "TIME_STAMP", nullable = false)
    open var timeStamp: Long = Instant.now().epochSecond
}