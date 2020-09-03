package com.andygeek.mapsproject

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.andygeek.mapsproject.database.AppDatabase
import com.andygeek.mapsproject.databinding.FragmentListBinding
import org.jetbrains.anko.doAsync


class ListFragment : Fragment() {
    lateinit var list : ListView
    private lateinit var mContext : Context
    private lateinit var db: AppDatabase
    private lateinit var listSave : ArrayList<Save>
    private lateinit var binding : FragmentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        listSave = arrayListOf()

        db = AppDatabase.getAppDatabase(mContext)!!


        doAsync {
            val data = db.placeDao().getAll()
            data.forEach {
                val saveTemp = Save(it.place_name, it.addess, it.place_name)
                listSave.add(saveTemp)
            }
            println(listSave)
        }

        // Wait for Data
        Handler().postDelayed(Runnable {
            val adapter = SaveAdapter(mContext, listSave)
            binding.listSave.adapter = adapter
            adapter.notifyDataSetChanged()
        }, 300)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
    }

}

