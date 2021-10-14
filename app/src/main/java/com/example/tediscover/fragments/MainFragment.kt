package com.example.tediscover.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.tediscover.viewmodels.LoginViewModel
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        observeAuthenticationState()
        binding = FragmentMainBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        observeAuthenticationState()

        binding.signUpBtn.setOnClickListener {
//            val action = MainFragmentDirections.actionMainFragmentToSignupFragment()
            findNavController().navigate(R.id.signupFragment)
        }
    }


    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: navigate to TED activity.
     * If there is no logged in user: show a login button, setup the click listener on the loginBtn
     */
    private fun observeAuthenticationState() {

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    val intent = Intent(requireContext(), TedActivity::class.java)
                    requireContext().startActivity(intent)
                    requireActivity().finish()
                }
                else -> {
                    binding.signInBtn.setOnClickListener {
//                        val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
            }
        })
    }
}