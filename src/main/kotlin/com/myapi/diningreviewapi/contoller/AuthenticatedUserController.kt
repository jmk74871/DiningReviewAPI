package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.TokenRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reg/user")
class AuthenticatedUserController(
    restaurantRepository: RestaurantRepository,
    userRepository: UserRepository,
    diningReviewRepository: DiningReviewRepository,
    tokenRepository: TokenRepository
) : AuthenticationController(restaurantRepository, userRepository, diningReviewRepository, tokenRepository) {
    // ToDo: build methods to submit new requests, edit and delete them. Maybe some additional views?
}