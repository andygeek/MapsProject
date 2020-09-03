package com.andygeek.mapsproject

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.andygeek.mapsproject.database.AppDatabase
import com.andygeek.mapsproject.databinding.FragmentDetailBinding
import com.andygeek.mapsproject.databinding.FragmentListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var list : ListView
    lateinit var mContext : Context
    lateinit var db: AppDatabase
    lateinit var list_save : MutableList<String>
    lateinit var adapter : ArrayAdapter<String>
    lateinit var binding : FragmentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate<FragmentListBinding>(inflater, R.layout.fragment_list, container, false)

        list_save = mutableListOf()

        db = AppDatabase.getAppDatabase(mContext)!!

        doAsync {
            println("XXXXXX")
            var data = db.placeDao().getAll()
            data.forEach {
                println("XXXXXX")
                println(it.place_id)
                list_save.add(it.place_name)
            }
            println(list_save)
        }


        binding.btnUpdate.setOnClickListener {
            adapter = ArrayAdapter(mContext, android.R.layout.simple_expandable_list_item_1, list_save)
            binding.listSave.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}

