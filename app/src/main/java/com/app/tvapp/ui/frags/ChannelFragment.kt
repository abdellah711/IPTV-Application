package com.app.tvapp.ui.frags

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tvapp.adapters.ChannelsPagingAdapter
import com.app.tvapp.databinding.FragmentChannelBinding
import com.app.tvapp.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM = "param"

class ChannelFragment : Fragment() {

    private lateinit var pagingAdapter: ChannelsPagingAdapter
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

        pagingAdapter = ChannelsPagingAdapter(viewModel)

        binding.recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@ChannelFragment.pagingAdapter
        }



        if(isFav) {
            viewModel.favs.observe(viewLifecycleOwner) {
                lifecycleScope.launch(Dispatchers.IO) {
                    pagingAdapter.submitData(it)
                }
            }
            pagingAdapter.addLoadStateListener {
                binding.shimmer.isVisible = false
                binding.emptyLayout.isVisible = pagingAdapter.itemCount == 0
            }
        }
        else {
            viewModel.channels.observe(viewLifecycleOwner) {
                lifecycleScope.launch(Dispatchers.IO) {
                    pagingAdapter.submitData(it)
                }
            }
            pagingAdapter.addLoadStateListener {
                binding.shimmer.isVisible = pagingAdapter.itemCount == 0
            }
        }
    }

    companion object {

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