package com.artileriya.uicomponents.repository

import androidx.lifecycle.LiveData
import com.artileriya.uicomponents.dao.UserDao
import com.artileriya.uicomponents.model.User
import javax.inject.Inject

interface UserRepoInterface {

}
class UserRepository @Inject constructor(private val userDao : UserDao) : UserRepoInterface {
    suspend fun insert(user : User) {
        userDao.insert(user)
    }
    fun getAll() : LiveData<List<User>> {
        return userDao.getAll()
    }
}