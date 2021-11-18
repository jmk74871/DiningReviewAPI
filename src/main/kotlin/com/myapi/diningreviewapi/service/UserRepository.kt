package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun existsByUserNameIs(userName: String): Boolean

    fun findByUserName(userName: String): Optional<User>

}