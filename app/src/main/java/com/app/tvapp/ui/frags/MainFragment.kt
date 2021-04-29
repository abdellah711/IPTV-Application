package com.app.tvapp.ui.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.app.tvapp.R
import com.app.tvapp.adapters.PagerAdapter
import com.app.tvapp.databinding.FragmentMainBinding
import com.app.tvapp.viewmodels.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val titles = listOf("All","Favorites")
        binding.apply {
            pager.adapter = PagerAdapter(requireActivity(), listOf(
                ChannelFragment.newInstance(),
                ChannelFragment.newInstance(true)
            ))
            TabLayoutMediator(tabs.tabs,pager){ tab, pos ->
                tab.text = titles[pos]
            }.attach()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}