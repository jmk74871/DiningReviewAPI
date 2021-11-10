package com.myapi.diningreviewapi.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ADMIN")
open class Admin{

    @Id
    @Column(name = "USER_ID", unique = true)
    open var userID: Long? = null

    @Column(name = "ADDED_BY", nullable = false)
    open var addedBy: String? = null

    @Column(name = "APPROVED_BY")
    open var approvedBy: String? = null

    @Column(name = "APPOVED", nullable = false)
    open var appoved: Boolean = false

    @Column(name = "IS_INITIAL_SUPER_ADMIN", nullable = false)
    open var isInitialSuperAdmin: Boolean = false
}