package com.app.tvapp.ui.frags

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tvapp.adapters.ChannelsAdapter
import com.app.tvapp.adapters.ChannelsPagingAdapter
import com.app.tvapp.databinding.FragmentSearchBinding
import com.app.tvapp.others.Resource
import com.app.tvapp.viewmodels.MainViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: ChannelsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    private fun setupRecycler() {

        adapter = ChannelsAdapter(viewModel)

        binding.recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@SearchFragment.adapter
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if(it is Resource.Success){
                adapter.channels = it.data
                binding.emptyLayout.isVisible = it.data.isEmpty()
            }else{
                //todo
                Log.e("Test","loading....")
            }

        }
    }

}