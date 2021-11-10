package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.DiningReview
import com.myapi.diningreviewapi.model.Restaurant
import com.myapi.diningreviewapi.service.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/admin")
class AdminController (
    private val restaurantRepository: RestaurantRepository,
    private val diningReviewRepository: DiningReviewRepository,
    private val tokenRepository: TokenRepository,
    private val adminRepository: AdminRepository
    )   {

        @GetMapping("/")
        fun getReviewsWaitingForApproval(@CookieValue("token", defaultValue="") tokenUuidString: String): List<DiningReview> {
            //check admin rights
            if (!this.validateToken(tokenUuidString)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Admin rights or not logged in.")

            // get reviews waiting for approval
            return this.diningReviewRepository.findByHasApprovalIsFalse()
        }



        // Helper methods


        //modified version for admin check
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

                // check for Admin rights:
                return token.userID?.let { this.adminRepository.existsByUserIDAndAppovedIsTrue(it) } == true

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
} // End of class