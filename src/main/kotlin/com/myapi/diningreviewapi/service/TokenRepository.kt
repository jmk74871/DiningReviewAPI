package com.myapi.diningreviewapi.service;

import com.myapi.diningreviewapi.model.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository : JpaRepository<Token, String> {

    fun findByUuidString(uuidString: String): Optional<Token>

}