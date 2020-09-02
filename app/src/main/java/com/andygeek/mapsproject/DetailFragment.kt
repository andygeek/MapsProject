package com.andygeek.mapsproject

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.andygeek.mapsproject.data_place.Place
import com.andygeek.mapsproject.databinding.FragmentDetailBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.net.URL

// Key for param
private const val ARG_PARAM = "PLACE_ID"

class DetailFragment : BottomSheetDialogFragment() {

    lateinit var binding : FragmentDetailBinding

    // Place id brought from MapFragment
    private var place_id: String? = null

    companion object{
        @JvmStatic
        fun newInstance(param: String) = DetailFragment().apply{
            arguments = Bundle().apply {
                putString(ARG_PARAM, param)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            place_id = it.getString(ARG_PARAM)
        }
        binding = DataBindingUtil.inflate<FragmentDetailBinding>(inflater, R.layout.fragment_detail, container, false)
        request_place(place_id.toString())
        return binding.root
    }

    fun request_place(place_id : String){
        val queue = Volley.newRequestQueue(context)
        var url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&key=AIzaSyAYuDoz5piHJyOJ996l344nzwhdAcYM2Wg"
        var stringRequest = StringRequest(Request.Method.GET, url, { response ->
            var gson = Gson()
            var obj = gson?.fromJson(response, Place::class.java)
            binding.txtId.text = obj.result.name
            binding.txtAddress.text = obj.result.formatted_address
            var first_photo = obj.result.photos.first()
            var reference = first_photo.photo_reference
            var url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=$reference&key=AIzaSyAYuDoz5piHJyOJ996l344nzwhdAcYM2Wg"
            Glide.with(this).load(url).into(binding.imgReference)
        }, {
            println("Error")
        })
        queue.add(stringRequest)
    }
}