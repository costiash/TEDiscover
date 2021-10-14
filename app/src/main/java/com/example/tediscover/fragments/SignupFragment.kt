package com.example.tediscover.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.tediscover.viewmodels.LoginViewModel
import com.example.tediscover.R
import com.example.tediscover.databinding.FragmentSignupBinding
import com.example.tediscover.firebase.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignupFragment : Fragment() {

    companion object {
        const val TAG = "SignupFragment"
    }

    private lateinit var binding: FragmentSignupBinding
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDbInstance: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        firebaseDbInstance = Firebase.firestore

        binding.registerBtn.setOnClickListener {
            val email = binding.filledTextFieldEmail.editText?.text!!.toString()
            val password = binding.filledTextFieldPassword.editText?.text!!.toString()
            val userName = binding.filledTextFieldUsername.editText?.text!!.toString()

            if (email.isNotBlank() and password.isNotBlank()) {
                launchSignupFlow(email, password, userName)
            } else {
                Toast.makeText(requireActivity().applicationContext,
                    "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // If the user presses the back button, bring them back to the home screen.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.mainFragment, false)
        }

        // Observe the authentication state so we can know if the user has logged in successfully.
        // If the user has logged in successfully, bring them back to the home screen.
        // If the user did not log in successfully, display an error message.
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> navController.popBackStack()

                else -> Log.e(
                    TAG,
                    "Authentication state that doesn't require any UI change $authenticationState"
                )
            }
        })
    }

    private fun launchSignupFlow(email: String, password: String, userName: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmailAndPassword:success")
                    val user = auth.currentUser
                    val currUser = UserData(user!!.uid, user.email!!, userName)
                    firebaseDbInstance.collection("users").document(user.uid).set(currUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(requireActivity().applicationContext,
                        "createUser Failed: Please make sure that you filled the fields correctly",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}