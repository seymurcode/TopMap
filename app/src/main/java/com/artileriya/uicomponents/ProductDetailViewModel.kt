package com.artileriya.uicomponents

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artileriya.uicomponents.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class ProductDetailViewModel @Inject constructor (var userRepository: UserRepository) : ViewModel() {
    var productList = mutableListOf<Product>()

    var updateListener = MutableLiveData<Boolean>()

    fun appendProductList(item : Product) {
        SessionHelper.cachedProductList.add(item)
        productList.addAll(SessionHelper.cachedProductList)
        updateListener.postValue(true)
    }
}