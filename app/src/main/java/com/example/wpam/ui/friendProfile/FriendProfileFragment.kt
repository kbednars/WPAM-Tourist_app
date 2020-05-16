package com.example.wpam.ui.friendProfile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wpam.R


class FriendProfileFragment : Fragment() {

    companion object {
        fun newInstance() =
            FriendProfileFragment()
    }

    private lateinit var viewModel: FriendProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FriendProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
