package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.DiningReview
import com.myapi.diningreviewapi.model.Restaurant
import com.myapi.diningreviewapi.model.User
import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.TokenRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1")
class AuthenticatedUserController(
    private val restaurantRepository: RestaurantRepository,
    private val diningReviewRepository: DiningReviewRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
)   {
    // Endpoints regarding reviews
    @PostMapping("/reviews/{restaurantId}")
    fun addReview(
        @PathVariable restaurantId: Long,
        @RequestBody review: DiningReview,
        @CookieValue("token", defaultValue = "") tokenUuidString: String
    ): DiningReview {

        // validate token
        val user = validateToken(tokenUuidString)

        review.restaurant = restaurantId
        review.submittingUser = user.userName
        return diningReviewRepository.save(review)
    }

    @PutMapping("/reviews/{reviewID}")
    fun updateReview(@PathVariable reviewID: Long,
                     @RequestBody reviewUpdate: DiningReview,
                     @CookieValue("token", defaultValue="") tokenUuidString: String): DiningReview {
        // validate token
        val user = validateToken(tokenUuidString)

        // get review by id
        val reviewOptional = this.diningReviewRepository.findById(reviewID)
        if(reviewOptional.isEmpty) throw ResponseStatusException(HttpStatus.BAD_REQUEST, " Could not find a review corresponding to the ID provided.")
        val reviewOriginal = reviewOptional.get()

        // check if updating user is same as submitting user
        if (review.submittingUser != user.userName) throw ResponseStatusException(HttpStatus.FORBIDDEN)

        // update the original review object
        reviewOriginal.foodRating = reviewUpdate.foodRating
        reviewOriginal.serviceRating = reviewUpdate.serviceRating
        reviewOriginal.comment = reviewUpdate.comment

        return this.diningReviewRepository.save(reviewOriginal)
    }

    @DeleteMapping("/reviews/{reviewID}")
    fun deleteReview(@PathVariable reviewID: Long,
                     @CookieValue("token", defaultValue="") tokenUuidString: String) {
        // validate token
        val user = validateToken(tokenUuidString)

        // get review by id
        val reviewOptional = this.diningReviewRepository.findById(reviewID)
        if(reviewOptional.isEmpty) throw ResponseStatusException(HttpStatus.BAD_REQUEST, " Could not find a review corresponding to the ID provided.")
        val reviewOriginal = reviewOptional.get()

        // check if updating user is same as submitting user
        if (review.submittingUser != user.userName) throw ResponseStatusException(HttpStatus.FORBIDDEN)

        // delete review
        return this.diningReviewRepository.delete(reviewOriginal)
    }

    // Endpoints regarding restaurants
    @PostMapping("/")
    fun addRestaurants(@RequestBody restaurant: Restaurant,
                       @CookieValue("token", defaultValue="") tokenUuidString: String): Restaurant {
        // validate token
        validateToken(tokenUuidString)
        /*
        ToDO: check for duplicate submissions?
         */

        return restaurantRepository.save(restaurant)
    }

    /*
    ToDo: Who may update restaurant entries or delete them?
     */


    // Helper methods
    // fun validateUserIsOriginalCreator

    fun validateToken(tokenUuidString: String): User {
        // check for empty inputString
        if(tokenUuidString == "") throw ResponseStatusException(HttpStatus.FORBIDDEN)

        // remove timed out tokens
        this.removeTimedOutTokens()

        // check if the submitted tokenString belongs to a valid token
        val tokenOptional = this.tokenRepository.findByUuidString(tokenUuidString)
        if(tokenOptional.isEmpty) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val token = tokenOptional.get()

        // update timestamp
        token.timeStamp = Instant.now().epochSecond
        this.tokenRepository.save(token)

        // get user object and return it
        val userOptional = token.userID?.let { this.userRepository.findById(it) } ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)
        if (userOptional.isEmpty) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        else return userOptional.get()
    }

    fun removeTimedOutTokens(){
        val timedOutTokens = this.tokenRepository.findByTimeStampIsGreaterThan(Instant.now().epochSecond + 20 * 60)

        for(token in timedOutTokens){
            this.tokenRepository.delete(token)
        }
    }
}

