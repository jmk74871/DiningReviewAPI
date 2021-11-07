package com.myapi.diningreviewapi.model;

import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<Token, String> {
}