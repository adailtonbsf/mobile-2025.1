package me.dalton.crudapp

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDAO) {
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
    suspend fun updateUser(user: User): Int {
        return userDao.updateUser(user)
    }
    suspend fun deleteUser(user: User): Int {
        return userDao.deleteUser(user)
    }
}