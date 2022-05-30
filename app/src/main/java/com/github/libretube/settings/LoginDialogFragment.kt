package com.github.libretube.settings

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.libretube.R
import com.github.libretube.data.network.PipedApiClient

class LoginDialogFragment : DialogFragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var apiClient: PipedApiClient
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // apiClient = PipedApiClient.initialize(requireContext(), LoginDialogFragment::class.toString())
        sharedPref = requireContext().getSharedPreferences("token", Context.MODE_PRIVATE)
        val builder = AlertDialog.Builder(requireActivity())
        // Get the layout inflater
        val inflater = requireActivity().layoutInflater
        val token = sharedPref.getString("token", "")
        val view: View

        if (!token.isNullOrBlank()) {
            view = inflater.inflate(R.layout.dialog_logout, null)

            view.findViewById<Button>(R.id.logout).setOnClickListener {
                Toast.makeText(context, R.string.loggedout, Toast.LENGTH_SHORT).show()
                with(sharedPref.edit()) {
                    remove("token")
                    apply()
                }
                dialog?.dismiss()
            }
        } else {
            view = inflater.inflate(R.layout.dialog_login, null)
            username = view.findViewById(R.id.username)
            password = view.findViewById(R.id.password)

            view.findViewById<Button>(R.id.login).setOnClickListener {
                if (!username.text.isNullOrBlank() && !password.text.isNullOrBlank()) {
                    login(username.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(context, R.string.empty, Toast.LENGTH_SHORT).show()
                }
            }

            view.findViewById<Button>(R.id.register).setOnClickListener {
                if (!username.text.isNullOrBlank() && !password.text.isNullOrBlank()) {
                    register(username.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(context, R.string.empty, Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun login(username: String, password: String) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.login(username, password)

            response.onSuccess {
                if (it.error.isNullOrEmpty()) {
                    Toast.makeText(context, R.string.loggedIn, Toast.LENGTH_SHORT).show()

                    with(sharedPref.edit()) {
                        putString("token", it.token)
                        apply()
                    }

                    dialog?.dismiss()
                } else {
                    Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                }
                // Handle server error
            }
        }
    }

    private fun register(username: String, password: String) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.register(username, password)

            response.onSuccess {
                if (it.error.isNullOrEmpty()) {
                    Toast.makeText(context, R.string.loggedIn, Toast.LENGTH_SHORT).show()

                    with(sharedPref.edit()) {
                        putString("token", it.token)
                        apply()
                    }

                    dialog?.dismiss()
                } else {
                    Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
