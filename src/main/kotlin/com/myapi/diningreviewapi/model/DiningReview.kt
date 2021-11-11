package com.myapi.diningreviewapi.model

import javax.persistence.*

@Table(name = "DINING_REVIEWS")
@Entity
open class DiningReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    open var id: Long? = null

    @Column(name = "SUBMITTING_USER", nullable = false)
    open var submittingUser: String? = null

    @Column(name = "RESTAURANT_ID", nullable = false)
    open var restaurant: Long? = null

    @Column(name = "FOOD_RATING", nullable = false)
    open var foodRating: Int? = 0

    @Column(name = "SERVICE_RATING", nullable = false)
    open var serviceRating: Int? = 0

    @Column(name = "COMMENT")
    open var comment: String? = null

    @Enumerated
    @Column(name = "STATUS", nullable = false)
    open var status: StatusEnum = StatusEnum.pending
}