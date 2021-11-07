package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.Token
import com.myapi.diningreviewapi.model.TokenRepository
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
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(restaurantRepository: RestaurantRepository, userRepository: UserRepository, diningReviewRepository: DiningReviewRepository, tokenRepository: TokenRepository) {

    private val restaurantRepository: RestaurantRepository
    private val userRepository: UserRepository
    private val diningReviewRepository: DiningReviewRepository
    private val tokenRepository: TokenRepository

    init {
        this.restaurantRepository = restaurantRepository
        this.userRepository = userRepository
        this.diningReviewRepository = diningReviewRepository
        this.tokenRepository = tokenRepository
    }

    @PostMapping("/signup")
    fun createNewUserAccount(@RequestBody user: User): User {
        if(user.userName?.let {this.userRepository.existsByUserNameIs(it)} == true){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is missing or already in use but must me unique. Please pick an other username.")
        }
        // toDo: add a check for password complexity and length?
        user.password = user.password?.let { toHashString(it) } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is missing.")
        return this.userRepository.save(user)
    }

    @PostMapping("/login")
    fun loginExistingUser(@RequestBody user: User, response: HttpServletResponse): HttpStatus {
        //Error message returned should always be the same, so it's saved to a val
        val loginFailed = ResponseStatusException(HttpStatus.BAD_REQUEST, "Login failed. Check your credentials!")

        //get the User from DB if an existing username was submitted
        val userOptional = user.userName?.let { this.userRepository.findByUserName(it) } ?: throw loginFailed
        if (!userOptional.isPresent) {throw loginFailed}
        val userFromDB = userOptional.get()

        // hash the password submitted and compare it with the one from DB
        val passwordSubmitted = user.password?.let {toHashString(it) } ?: throw loginFailed
        if (passwordSubmitted != userFromDB.password) {
            // throw exception if passwords don't match
            throw loginFailed
        } else {
            // ToDo: how to handle multiple logins while valid token still exists?

            // create token and store it
            val token = Token()
            token.userID = userFromDB.id
            this.tokenRepository.save(token)

            // create a cookie and return it.

            // ToDo: If ever used live the cookie might need to have cookie.secure & cookie.httpOnly set to

            val cookie = Cookie("token", token.uuidString)
            cookie.maxAge = 20 * 60 // 20 Minutes
            cookie.path = "/api/v1/reg/user/"
            response.addCookie(cookie)

            return HttpStatus.OK
        }
    }

    fun toHashString(inputString: String): String {
        // turn String into ByteArray and compute the hashes
        val hashBytes: ByteArray? = MessageDigest.getInstance("SHA-384").digest(inputString.toByteArray())
        // turn hashed Bytes bak into a String
        return BigInteger(1, hashBytes).toString(16)

    }

    fun validateToken(): Boolean{
        // ToDo: add method to check if a token is still valid.
        return false
    }

    fun removeTimedOutTokens(){
        // ToDo: add method to remove all outdated Tokens from DB.
    }

}