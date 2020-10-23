package com.mp.testapp.getupside.presentation.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mp.testapp.getupside.R
import com.mp.testapp.getupside.presentation.list.FoodPointListFragment
import com.mp.testapp.getupside.presentation.map.MapFragment
import java.lang.IllegalArgumentException

class MainPageAdapter(
        private val context: Context,
        fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = SCREEN_NUMBER

    override fun getItem(position: Int): Fragment = when(position) {
        MAP -> MapFragment()
        LIST -> FoodPointListFragment()
        else -> throw IllegalArgumentException("Invalid screen position: $position")
    }

    override fun getPageTitle(position: Int): CharSequence? = when(position) {
        MAP -> context.resources.getString(R.string.map)
        LIST -> context.resources.getString(R.string.list)
        else -> throw IllegalArgumentException("Invalid screen position: $position")
    }

    companion object {
        private const val SCREEN_NUMBER = 2
        private const val MAP = 0
        private const val LIST = 1
    }
}