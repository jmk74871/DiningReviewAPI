package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.Restaurant
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RestaurantRepository : CrudRepository<Restaurant, Long> {


}