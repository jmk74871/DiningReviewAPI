package com.myapi.diningreviewapi.model

import javax.persistence.*

@Table(name = "USERS")
@Entity
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    open var id: Long? = null

    @Column(name = "USER_NAME", nullable = false, unique = true)
    open var userName: String? = null

    @Column(name = "CITY", nullable = false)
    open var city: String? = null

    @Column(name = "PLZ", nullable = false)
    open var plz: String? = null

    @Column(name = "IS_VEGETARIAN", nullable = false)
    open var isVegetarian: Boolean? = false

    @Column(name = "PASSWORD", nullable = false)
    open var password: String? = null
}