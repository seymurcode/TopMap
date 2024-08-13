package com.artileriya.uicomponents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.artileriya.uicomponents.databinding.FragmentMenuBinding
import com.artileriya.uicomponents.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {


    val viewModel : MenuViewModel by viewModels()

    /*val viewModel: MenuViewModel by viewModels() {
        MenuViewModelFactory((activity?.application as MyApplication).repository)
    }*/

    lateinit var binding : FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.insert(User(0, "Sefa", "Ayçiçek"))

      /*  context?.let {


            print("Hello-1")
            runBlocking {
                launch {
                    repository.insert(User(0, "Sefa", "Ayçiçek"))
                    repository.getAll().observe(this@MenuFragment.viewLifecycleOwner) { userList->
                        print(userList)
                    }
                    print("Hello-3")
                }

                launch {

                }
            }
        }*/

        print("Hello-2")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun openProductPage() {
        //val action = MenuFragmentDirections.actionMenuToProducts()
        setFragmentResultListener(ProductDetail.resultKey){ requestKey, bundle ->
            print(bundle)
        }
        viewModel.nameText.value?.let {
            val product = Product("ürün_adi")
            val action = MenuFragmentDirections.actionMenuToProductDetail(it, product)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.navigateNextPageEvent.observe(this) {
            if(it) {
                openProductPage()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.navigateNextPageEvent.removeObservers(this)
        viewModel.navigateNextPageEvent.postValue(false)
    }
}