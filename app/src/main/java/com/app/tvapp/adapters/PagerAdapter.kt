package com.app.tvapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    fa: FragmentActivity,
    private val frags: List<Fragment>
) : FragmentStateAdapter(fa) {

    override fun getItemCount() = frags.size

    override fun createFragment(position: Int) = frags[position]

}