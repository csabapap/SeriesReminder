package hu.csabapap.seriesreminder.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import javax.inject.Inject

class AccountFragment : DaggerFragment() {

    @Inject
    lateinit var loggedInUserRepository: LoggedInUserRepository

    lateinit var connectButton: Button

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
                findNavController().navigate(R.id.action_navigation_account_to_tratk_auth_fragment)
//              activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_navigation_account_to_tratk_auth_fragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (loggedInUserRepository.isLoggedIn()) {
            connectButton.text = "disconnect from trakt"
        }
    }
}