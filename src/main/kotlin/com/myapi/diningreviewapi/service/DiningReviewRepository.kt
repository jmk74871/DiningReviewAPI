package com.myapi.diningreviewapi.service

import com.myapi.diningreviewapi.model.DiningReview
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DiningReviewRepository : CrudRepository<DiningReview, Long> {

}