package com.artileriya.uicomponents.di

import com.artileriya.uicomponents.repository.ProductRepository
import com.artileriya.uicomponents.repository.ProductRepositoryInterface
import com.artileriya.uicomponents.repository.UserRepoInterface
import com.artileriya.uicomponents.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    abstract fun bindRepository(prm : UserRepository) : UserRepoInterface

    @Binds
    abstract fun bindProductRepository(prm : ProductRepository) : ProductRepositoryInterface
}