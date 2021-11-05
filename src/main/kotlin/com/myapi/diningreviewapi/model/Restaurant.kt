package com.myapi.diningreviewapi.model

import javax.persistence.*

@Table(name = "RESTAURANTS")
@Entity
open class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    open var id: Long? = null

    @Column(name = "NAME", nullable = false)
    open var name: String? = null

    @Column(name = "ADRESS")
    open var adress: String? = null

    @Column(name = "CITIY", nullable = false)
    open var citiy: String? = null

    @Column(name = "PLZ", nullable = false)
    open var plz: String? = null

    @Column(name = "OVERALL_SCORE")
    open var overallScore: Double? = null

    @Column(name = "FOOD_SCORE")
    open var foodScore: Double? = null

    @Column(name = "SERVICE_SCORE")
    open var serviceScore: Double? = null
}