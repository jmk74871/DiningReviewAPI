package com.myapi.diningreviewapi.service;

import com.myapi.diningreviewapi.model.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin, Long> {

    fun findByUserIDAndAppovedIsTrue(userID: Long): Admin


    fun existsByUserIDAndAppovedIsTrue(userID: Long): Boolean

}