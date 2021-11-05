package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface UserRepository : CrudRepository<User, Long> {

}