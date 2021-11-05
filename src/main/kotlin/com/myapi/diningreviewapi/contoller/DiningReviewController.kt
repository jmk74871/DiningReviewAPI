package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.Restaurant
import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/v1")
class DiningReviewController(restaurantRepository: RestaurantRepository, userRepository: UserRepository, diningReviewRepository: DiningReviewRepository) {

    final val restaurantRepository: RestaurantRepository
    final val userRepository: UserRepository
    final val diningReviewRepository: DiningReviewRepository


    init {
        this.restaurantRepository = restaurantRepository
        this.userRepository = userRepository
        this.diningReviewRepository = diningReviewRepository
    }

    @GetMapping("/")
    fun getRestaurants(): MutableIterable<Restaurant> {
        return this.restaurantRepository.findAll()
    }

    @GetMapping("/{id}")
    fun getRestaurantsById(@PathVariable id: Int): Restaurant {

        val restaurant: Optional<Restaurant> = this.restaurantRepository.findById(id.toLong())
        if(restaurant.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the restaurant requested!")
        }
        return restaurant.get()
    }

    @PostMapping("/addRestaurant")
    fun addRestaurants(@RequestBody restaurant: Restaurant): Restaurant = restaurantRepository.save(restaurant)

}