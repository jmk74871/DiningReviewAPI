package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.DiningReview
import com.myapi.diningreviewapi.model.StatusEnum
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DiningReviewRepository : CrudRepository<DiningReview, Long> {


    fun findByRestaurant(restaurant: Long): MutableIterator<DiningReview>


    fun findDiningReviewByStatusIs(status: StatusEnum): List<DiningReview>


    fun findDiningReviewByRestaurantAndStatusIs(restaurant: Long, status: StatusEnum): List<DiningReview>

}