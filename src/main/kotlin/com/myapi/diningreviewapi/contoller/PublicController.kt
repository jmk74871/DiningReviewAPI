package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.DiningReview
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
class PublicController(restaurantRepository: RestaurantRepository, userRepository: UserRepository, diningReviewRepository: DiningReviewRepository) {

    private val restaurantRepository: RestaurantRepository
    private val userRepository: UserRepository
    private val diningReviewRepository: DiningReviewRepository

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

        val restaurantOptional: Optional<Restaurant> = this.restaurantRepository.findById(id.toLong())
        if(restaurantOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the restaurant requested!")
        }

        return restaurantOptional.get()


    }

    @GetMapping("/reviews/{restaurantId}")
    fun getReviewsByRestaurant(@PathVariable restaurantId: Int): List<DiningReview> {

        // check if restaurant exists:
        val restaurantOptional: Optional<Restaurant> = this.restaurantRepository.findById(restaurantId.toLong())
        if(restaurantOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the restaurant requested!")
        }

        // find reviews and return:
        return this.diningReviewRepository.findDiningReviewByRestaurantAndHasApprovalIsTrue(restaurantId.toLong())
    }



    // These Methods will be moved to the protected User Controller once created
    @PostMapping("/reviews/{restaurantId}")
    fun addReview(@PathVariable restaurantId: Long, @RequestBody review: DiningReview): DiningReview{
        review.restaurant = restaurantId
        return this.diningReviewRepository.save(review)
    }

    @PostMapping("/")
    fun addRestaurants(@RequestBody restaurant: Restaurant): Restaurant = restaurantRepository.save(restaurant)

}