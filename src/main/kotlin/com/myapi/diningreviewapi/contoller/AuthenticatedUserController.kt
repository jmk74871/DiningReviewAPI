package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.DiningReview
import com.myapi.diningreviewapi.model.Restaurant
import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.TokenRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/")
class AuthenticatedUserController(
    private val restaurantRepository: RestaurantRepository,
    private val diningReviewRepository: DiningReviewRepository,
    private val tokenRepository: TokenRepository
)   {
    // ToDo: build methods to submit, edit and delete reviews. Maybe some additional views?

    @PostMapping("/reviews/{restaurantId}")
    fun addReview(@PathVariable restaurantId: Long, @RequestBody review: DiningReview, @CookieValue("token", defaultValue="") tokenUuidString: String): DiningReview {

        // validate token
        if(!this.validateToken(tokenUuidString)) throw ResponseStatusException(HttpStatus.FORBIDDEN)

        review.restaurant = restaurantId
        val savedReview = this.diningReviewRepository.save(review)
        this.updateTotalRatings(restaurantId)  /* ToDo: move to admin area so update runs after approval */
        return savedReview
    }

    @PostMapping("/")
    fun addRestaurants(@RequestBody restaurant: Restaurant, @CookieValue("token", defaultValue="") tokenUuidString: String) {
        // validate token
        if(!this.validateToken(tokenUuidString)) throw ResponseStatusException(HttpStatus.FORBIDDEN)

        restaurantRepository.save(restaurant)
    }


    // Helper methods
    fun validateToken(tokenUuidString: String): Boolean{
        // check for empty inputString
        if(tokenUuidString == "") return false

        // remove timed out tokens
        this.removeTimedOutTokens()

        // check if the submitted tokenString belongs to a valid token
        val tokenOptional = this.tokenRepository.findByUuidString(tokenUuidString)
        if(tokenOptional.isPresent){
            val token = tokenOptional.get()

            // update timestamp
            token.timeStamp = Instant.now().epochSecond
            this.tokenRepository.save(token)

            return true
        }else return false
    }

    fun removeTimedOutTokens(){
        val timedOutTokens = this.tokenRepository.findByTimeStampIsGreaterThan(Instant.now().epochSecond + 20 * 60)

        for(token in timedOutTokens){
            this.tokenRepository.delete(token)
        }
    }

    fun updateTotalRatings(restaurantId: Long){
        // get the restaurant
        val restaurantOptional = this.restaurantRepository.findById(restaurantId)
        if(restaurantOptional.isEmpty) return
        val restaurant: Restaurant = restaurantOptional.get()

        // find all associated reviews
        val reviews = this.diningReviewRepository.findDiningReviewByRestaurantAndHasApprovalIsTrue(restaurantId)
        if(reviews.isEmpty()) return

        // sum up the ratings
        val sumFoodRatings = reviews.mapNotNull(DiningReview::foodRating)
        val sumServiceRatings = reviews.mapNotNull(DiningReview::serviceRating)

        // calculate the Scores and update object accordingly
        if(sumFoodRatings.isNotEmpty()){
            restaurant.foodScore = sumFoodRatings.sum().toDouble() / sumFoodRatings.size.toDouble()
        }
        if(sumServiceRatings.isNotEmpty()){
            restaurant.serviceScore = sumServiceRatings.sum().toDouble() / sumServiceRatings.size.toDouble()
        }
        restaurant.serviceScore?.let { serviceScore -> restaurant.foodScore?.let { foodScore -> restaurant.overallScore = (foodScore + serviceScore) / 2 } }

        // save to db
        this.restaurantRepository.save(restaurant)
    }

}

