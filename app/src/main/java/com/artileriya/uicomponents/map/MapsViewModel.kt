package com.artileriya.uicomponents.map



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artileriya.uicomponents.model.User
import com.artileriya.uicomponents.repository.ProductRepository
import com.artileriya.uicomponents.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor (var userRepository: UserRepository,
                                         var productRepository: ProductRepository) : ViewModel() {
    var nameText = MutableLiveData<String>()
    var navigateNextPageEvent = MutableLiveData<Boolean>()

    fun onButtonClick() {
        navigateNextPageEvent.postValue(true)
    }

    fun getAllData(): LiveData<List<User>> {
        return userRepository.getAll()
    }
    fun insert(user: User) {
        viewModelScope.launch {
            userRepository.insert(user)
        }
    }
}