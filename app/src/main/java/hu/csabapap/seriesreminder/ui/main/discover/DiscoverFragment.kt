package hu.csabapap.seriesreminder.ui.main.discover

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import hu.csabapap.seriesreminder.R

class DiscoverFragment : Fragment() {

    private var listType: Int? = null

    private var mListener: OnFragmentInteractionListener? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            listType = arguments!!.getInt(ARG_DISCOVER_TYPE)
        }
    }

    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    public override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException((context!!.toString() + " must implement OnFragmentInteractionListener"))
        }
    }

    public override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        val TYPE_TRENDING = 1
        val TYPE_POPLAR = 2

        private val ARG_DISCOVER_TYPE = "discover_type"

        fun newInstance(type: Int): DiscoverFragment {
            val fragment = DiscoverFragment()
            val args = Bundle()
            args.putInt(ARG_DISCOVER_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
