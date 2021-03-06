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
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val restaurantRepository: RestaurantRepository,
    private val userRepository: UserRepository,
    private val diningReviewRepository: DiningReviewRepository,
    private val tokenRepository: TokenRepository
)   {

    @PostMapping("/signup")
    fun createNewUserAccount(@RequestBody user: User): User {
        if(user.userName?.let {this.userRepository.existsByUserNameIs(it)} == true){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is missing or already in use but must me unique. Please pick an other username.")
        }
        // check input
        if(user.userName == null || user.userName == "" || user.city == null || user.city == "" || user.plz == null || user.plz == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "required input missing or empty")
        }

        // check for password complexity and length - needs at least 6 characters including one uppercase, one lowercase and one numeric
        user.password?.let { pw -> if(pw.length < 6 || pw.none { char -> char in '0'..'9' } || pw.none {char -> char.isLowerCase()} || pw.none {char -> char.isUpperCase()}) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password complexity requirements not met. Needs at least 6 characters including one uppercase, one lowercase and one numeric")
        } } ?:  throw ResponseStatusException(HttpStatus.BAD_REQUEST, "required input missing or empty")

        user.password = user.password?.let { toHashString(it) } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "required input missing or empty")
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
    fun logoutUser(@CookieValue(value = "token", defaultValue = "") tokenUuidString: String): HttpStatus {
        // checks for missing input
        if(tokenUuidString == "") throw ResponseStatusException(HttpStatus.BAD_REQUEST, "no token found, logout not necessary")

        // finds the token if existing and deletes it
        if(this.tokenRepository.findByUuidString(tokenUuidString).isPresent) {
            this.tokenRepository.delete(this.tokenRepository.findByUuidString(tokenUuidString).get())
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
}