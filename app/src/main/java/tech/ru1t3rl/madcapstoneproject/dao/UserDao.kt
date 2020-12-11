package tech.ru1t3rl.madcapstoneproject.dao

import tech.ru1t3rl.madcapstoneproject.model.User

interface UserDao {
    fun getAllUsers(): List<User>
    fun addUser(user: User): String
    fun updateUser(user: User)
    fun getUser(id: String): User?
}