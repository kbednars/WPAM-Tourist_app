package com.example.wpam.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wpam.R
import com.example.wpam.ui.points.GetPointsViewModel


class Search : Fragment() {


    companion object {
        fun newInstance() = Search()
    }

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("MyTAG", "Jestem w on Create Search")
       val root =  inflater.inflate(R.layout.search_fragment, container, false)
        var textView :TextView = root.findViewById(R.id.textView3)
        val bundle = this.arguments
        textView.text = (bundle?.getString("notificationId"))
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onPause() {
        super.onPause()
        var viewModel2 = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
        viewModel2.search_pause.value = true;
        Log.i("MyTAG", "Jestem w pause Search")

    }

    override fun onResume() {
        Log.i("MyTAG", "Jestem w resume Search")
        var viewModel2 = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
        viewModel2.search_pause.value = false;
        super.onResume()
    }
}
