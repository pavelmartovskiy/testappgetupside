package com.mp.testapp.getupside.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mp.testapp.getupside.R
import com.mp.testapp.getupside.domain.FoodPoint
import com.mp.testapp.getupside.domain.FoodPointsSearchError
import com.mp.testapp.getupside.domain.FoodPointsSearchSuccess
import com.mp.testapp.getupside.presentation.main.MainActivity
import com.mp.testapp.getupside.presentation.main.MainViewModel
import kotlinx.android.synthetic.main.f_list.view.*

class FoodPointListFragment : Fragment() {

    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.f_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun List<FoodPoint>.map() = map { it.map() }

    fun FoodPoint.map() = FoodPointItem(
            latitude, longitude, name, address, phone, distance.toString()
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (requireActivity() as MainActivity).getViewModel()
        val adapter = FoodPointListAdapter()
        requireView().apply {
            rv.adapter = adapter
        }
        viewModel.followFoodPoints().observe(this) { result ->
            when (result) {
                is FoodPointsSearchSuccess -> {
                    adapter.submitList(result.points.map())
                }
                is FoodPointsSearchError -> Snackbar.make(requireView(), result.message, BaseTransientBottomBar.LENGTH_LONG).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.requestFoodPoints()
    }
}