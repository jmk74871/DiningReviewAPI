package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.DiningReview
import com.myapi.diningreviewapi.model.Restaurant
import com.myapi.diningreviewapi.model.StatusEnum
import com.myapi.diningreviewapi.service.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

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
            return this.diningReviewRepository.findDiningReviewByStatusIs(StatusEnum.pending)
        }

        @PostMapping("/app/{reviewID}")
        fun approveReview(@PathVariable reviewID: Long, @CookieValue("token", defaultValue="") tokenUuidString: String): DiningReview {
            //check admin rights
            if (!this.validateToken(tokenUuidString)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Admin rights or not logged in.")

            // get review from db
            val review = this.getReviewFromDB(reviewID)

            //set approval to true and save
            review.status = StatusEnum.approved
            this.diningReviewRepository.save(review)
            review.restaurant?.let { this.updateTotalRatings(it) }
            return review
        }

        @PostMapping("/dis/{reviewID}")
        fun dismissReview(@PathVariable reviewID: Long, @CookieValue("token", defaultValue="") tokenUuidString: String): DiningReview {
            //check admin rights
            if (!this.validateToken(tokenUuidString)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Admin rights or not logged in.")

            val review = this.getReviewFromDB(reviewID)

            //set approval to true and save
            review.status = StatusEnum.dismissed
            return this.diningReviewRepository.save(review)
        }

        /*
        ToDo: add Endpoints to add and approve new admins. Same system as with reviews. Only init superadmin shall add and approve all by himself.

        ToDO: update readme

        ToDo: Think about building an additional controller to give restaurant owners the possibility to vew data about their restaurant.
         */


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
        val reviews = this.diningReviewRepository.findDiningReviewByRestaurantAndStatusIs(restaurantId, StatusEnum.approved)
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

        fun getReviewFromDB(reviewID: Long): DiningReview{
            val reviewOptional: Optional<DiningReview> = this.diningReviewRepository.findById(reviewID)
            if(reviewOptional.isEmpty) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "selected review not found")
            return reviewOptional.get()
        }
} // End of class