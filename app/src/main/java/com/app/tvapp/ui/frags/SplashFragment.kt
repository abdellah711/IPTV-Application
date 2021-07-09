package com.app.tvapp.ui.frags

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.tvapp.R
import com.app.tvapp.databinding.FragmentSplashBinding
import com.app.tvapp.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var valueAnimator: ValueAnimator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadingTxtAnimation()

        viewModel.isFetching.observe(viewLifecycleOwner){
            if(it == false){
                lifecycleScope.launch {
                    delay(500)
                    valueAnimator.cancel()
                    findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
                }
            }
        }
    }

    private fun setupLoadingTxtAnimation() {
        val baseTxt = binding.loadingTv.text.toString()

        valueAnimator = ValueAnimator.ofInt(0,1,2,3).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            start()
        }
        valueAnimator.addUpdateListener {
            var loadingString = baseTxt
            for(i in 0..(it.animatedValue as Int)) loadingString+="."
            binding.loadingTv.text = loadingString
        }
        binding.loadingTv
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}