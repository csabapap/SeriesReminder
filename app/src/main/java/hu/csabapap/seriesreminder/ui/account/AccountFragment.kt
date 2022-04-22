package hu.csabapap.seriesreminder.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import javax.inject.Inject

class AccountFragment : DaggerFragment() {

    @Inject
    lateinit var loggedInUserRepository: LoggedInUserRepository

    lateinit var connectButton: Button
    lateinit var welcomeMessage: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectButton = view.findViewById<Button>(R.id.connect_trakt).apply {
            setOnClickListener {
                if (loggedInUserRepository.isLoggedIn()) {
                    loggedInUserRepository.logout()
                    return@setOnClickListener
                }
                findNavController().navigate(R.id.action_navigation_account_to_tratk_auth_fragment)
            }
        }
        welcomeMessage = view.findViewById(R.id.welcome_tv)
    }

    override fun onResume() {
        super.onResume()
        loggedInUserRepository.onUserStateChanged = {isLoggedIn ->
            updateTraktAuthButton(isLoggedIn)
            displayWelcomeMessage(isLoggedIn)
        }
        displayWelcomeMessage(loggedInUserRepository.isLoggedIn())
        updateTraktAuthButton(loggedInUserRepository.isLoggedIn())
    }

    private fun displayWelcomeMessage(loggedIn: Boolean) {
        if (loggedIn) {
            welcomeMessage.visibility = View.VISIBLE
            welcomeMessage.text = "Hello, ${loggedInUserRepository.loggedInUser()?.username}"
        } else {
            welcomeMessage.visibility = View.GONE
        }
    }

    private fun updateTraktAuthButton(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            connectButton.text = "disconnect from trakt"
        } else {
            connectButton.text = "log in to trakt.tv"
        }
    }
}