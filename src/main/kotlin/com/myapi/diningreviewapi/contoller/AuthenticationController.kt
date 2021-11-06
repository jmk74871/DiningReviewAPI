package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.User
import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger
import java.security.MessageDigest


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(restaurantRepository: RestaurantRepository, userRepository: UserRepository, diningReviewRepository: DiningReviewRepository) {

    private val restaurantRepository: RestaurantRepository
    private val userRepository: UserRepository
    private val diningReviewRepository: DiningReviewRepository

    init {
        this.restaurantRepository = restaurantRepository
        this.userRepository = userRepository
        this.diningReviewRepository = diningReviewRepository
    }

    @PostMapping("/signup")
    fun createNewUserAccount(@RequestBody user: User): User {
        if(user.userName?.let {this.userRepository.existsByUserNameIs(it)} == true){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is missing or already in use but must me unique. Please pick an other username.")
        }
        // toDo: add a check for password complexitiy and lenght?
        user.password = user.password?.let { toHashString(it) } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is missing.")
        return this.userRepository.save(user)
    }

    @PostMapping("/login")
    fun loginExistingUser(@RequestBody user: User){

    }

    fun toHashString(inputString: String): String {
        // turn String into ByteArray and compute the hashes
        val hashBytes: ByteArray? = MessageDigest.getInstance("SHA-384").digest(inputString.toByteArray())
        // turn hashed Bytes bak into a String
        return BigInteger(1, hashBytes).toString(16)

    }

}