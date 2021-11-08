package com.myapi.diningreviewapi.contoller

import com.myapi.diningreviewapi.model.Token
import com.myapi.diningreviewapi.service.TokenRepository
import com.myapi.diningreviewapi.model.User
import com.myapi.diningreviewapi.service.DiningReviewRepository
import com.myapi.diningreviewapi.service.RestaurantRepository
import com.myapi.diningreviewapi.service.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/v1/auth")
open class AuthenticationController(restaurantRepository: RestaurantRepository, userRepository: UserRepository, diningReviewRepository: DiningReviewRepository, tokenRepository: TokenRepository) {

    protected val restaurantRepository: RestaurantRepository
    protected val userRepository: UserRepository
    protected val diningReviewRepository: DiningReviewRepository
    protected val tokenRepository: TokenRepository

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
            cookie.path = "/api/v1/"
            response.addCookie(cookie)

            return HttpStatus.OK
        }
    }

    @Throws(ResponseStatusException::class)
    @GetMapping("/logout")
    fun logoutUser(@CookieValue(value = "token", defaultValue = "") token: String): HttpStatus {
        // checks for missing input
        if(token == "") throw ResponseStatusException(HttpStatus.BAD_REQUEST, "no token found, logout not necessary")

        // finds the token if existing and deletes it
        if(this.tokenRepository.findByUuidString(token).isPresent) {
            this.tokenRepository.delete(this.tokenRepository.findByUuidString(token).get())
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "no token found, logout not necessary")
        }
        return HttpStatus.OK

    }

    fun toHashString(inputString: String): String {
        // turn String into ByteArray and compute the hashes
        val hashBytes: ByteArray? = MessageDigest.getInstance("SHA-384").digest(inputString.toByteArray())
        // turn hashed Bytes bak into a String
        return BigInteger(1, hashBytes).toString(16)

    }

    fun validateToken(tokenUuidString: String): Boolean{
        // check for empty inputString
        if(tokenUuidString == "") return false

        // remove timed out tokens
        this.removeTimedOutTokens()

        // check if the submitted tokenString belongs to a valid token
        return this.tokenRepository.findByUuidString(tokenUuidString).isPresent
    }

    fun removeTimedOutTokens(){
        val timedOutTokens = this.tokenRepository.findByTimeStampIsGreaterThan(Instant.now().epochSecond + 20 * 60)

        for(token in timedOutTokens){
            this.tokenRepository.delete(token)
        }
    }

}