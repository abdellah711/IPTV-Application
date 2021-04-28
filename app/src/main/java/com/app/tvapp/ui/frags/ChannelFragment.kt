package com.app.tvapp.ui.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tvapp.R
import com.app.tvapp.adapters.ChannelsAdapter
import com.app.tvapp.databinding.FragmentChannelBinding
import com.app.tvapp.viewmodels.MainViewModel

private const val ARG_PARAM = "param"

class ChannelFragment : Fragment() {

    private lateinit var adapter: ChannelsAdapter
    private val viewModel: MainViewModel by activityViewModels()

    private var isFav = false

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFav = it.getBoolean(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(inflater,container,false)
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
            adapter = this@ChannelFragment.adapter
        }
        if(isFav)
            viewModel.favs.observe(viewLifecycleOwner) {
                adapter.channels = it
                binding.emptyLayout.isVisible = it.isEmpty()
            }
        else
            viewModel.channels.observe(viewLifecycleOwner) {
                adapter.channels = it
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(fav: Boolean = false) =
            ChannelFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM, fav)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}