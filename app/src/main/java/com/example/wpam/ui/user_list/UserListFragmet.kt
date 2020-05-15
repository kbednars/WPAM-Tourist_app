package com.example.wpam.ui.user_list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wpam.R


class UserListFragmet : Fragment() {

    companion object {
        fun newInstance() = UserListFragmet()
    }

    private lateinit var viewModel: UserListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_list, container, false)
        var textView : TextView = root.findViewById(R.id.textView4)
        val bundle = this.arguments
        textView.text = (bundle?.getString("notificationId"))
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
