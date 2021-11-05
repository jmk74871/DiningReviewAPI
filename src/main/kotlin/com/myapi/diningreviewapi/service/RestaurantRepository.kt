package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.Restaurant
import org.springframework.data.repository.CrudRepository

interface RestaurantRepository : CrudRepository<Restaurant, Long> {

    fun existsByIdIs(id: Long): Boolean

}