package com.artileriya.uicomponents.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.artileriya.uicomponents.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user : User)

    @Query("SELECT * FROM user")
    fun getAll() : LiveData<List<User>>

    @Query("SELECT * FROM user where first_name LIKE :name AND last_name LIKE :lastname")
    fun findWithName(name: String, lastname : String) : LiveData<List<User>>

    @Delete
    suspend fun delete(user : User)

    //CRUD işlemleri
}