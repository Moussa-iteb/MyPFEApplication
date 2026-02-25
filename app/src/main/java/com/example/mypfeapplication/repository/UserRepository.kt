package com.example.mypfeapplication.repository

import com.example.mypfeapplication.model.User

class UserRepository {

    fun getUser(): User {
        return User(
            id = 1,
            name = "Ahmed",
            email = "ahmed@example.com"
        )
    }
}