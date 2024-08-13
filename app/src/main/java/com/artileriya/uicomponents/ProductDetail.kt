
package com.artileriya.uicomponents

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artileriya.uicomponents.databinding.FragmentProductDetailBinding
import kotlinx.parcelize.Parcelize

class ProductDetail : Fragment() {

    lateinit var viewModel : ProductDetailViewModel
    val args : ProductDetailArgs by navArgs()

    lateinit var binding : FragmentProductDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)
        binding = FragmentProductDetailBinding.inflate(inflater)
        binding.btn.setOnClickListener {
            /* val bundle = bundleOf("prm1" to 10, "prm2" to "Sefa", "prm3" to 10.2)

             setFragmentResult(resultKey, bundle)
             findNavController().popBackStack()*/

            val action = ProductDetailDirections.actionProductToCart()
            findNavController().navigate(action)
        }

        binding.txtName.text = args.nameParam
        viewModel.appendProductList(args.product)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.edit -> {
                    true
                }
                else -> {
                    true
                }
            }
        }

        viewModel.updateListener.observe(this) {
            if(it) {

            }
        }

        val productParam = args.product


        return binding.root
    }

    companion object {
        val resultKey = "RESULT"
    }
}

@Parcelize
data class Product(var name : String) : Parcelable
