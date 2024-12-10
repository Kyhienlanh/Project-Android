package com.example.projectdemo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Initialize Firebase Auth and Database
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Date of Birth EditText
        val editTextDOB = view.findViewById<EditText>(R.id.editTextDOB)
        editTextDOB.setOnClickListener {
            showDatePickerDialog(editTextDOB)
        }

        // Navigation to Login Fragment
        val textAuthor = view.findViewById<TextView>(R.id.textAuthor)
        textAuthor.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // EditTexts for Registration
        val editTextEmail = view.findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = view.findViewById<EditText>(R.id.editTextConfirmPassword)
        val editTextFullName = view.findViewById<EditText>(R.id.editTextFullName)
        val textViewForgotPassword=view.findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_forgotPasswordFragment2)
        }
        // Register Button
        val buttonRegister = view.findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            val fullName = editTextFullName.text.toString()
            val dob = editTextDOB.text.toString()
            val gender = when (view.findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId) {
                R.id.radioButtonMale -> "Male"
                R.id.radioButtonFemale -> "Female"
                R.id.radioButtonOther -> "Other"
                else -> ""
            }

            // Validate Input Fields
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && fullName.isNotEmpty() && dob.isNotEmpty()) {
                if (password == confirmPassword) {
                    // Create User with Email and Password
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            if (userId != null) {
                                val user = User(userId, fullName, email, dob, gender)
                                database.child(userId).setValue(user).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(requireContext(), "Account created and user data saved successfully!", Toast.LENGTH_SHORT).show()
                                        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to save user data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
