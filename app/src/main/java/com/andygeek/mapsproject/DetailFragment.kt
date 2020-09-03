package com.andygeek.mapsproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.andygeek.mapsproject.data_place.Place
import com.andygeek.mapsproject.database.AppDatabase
import com.andygeek.mapsproject.database.PlaceDao
import com.andygeek.mapsproject.databinding.FragmentDetailBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Key for param
private const val ARG_PARAM = "PLACE_ID"

class DetailFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentDetailBinding
    private var db: AppDatabase? = null
    private var placeDao: PlaceDao? = null
    lateinit var  obj : Place
    lateinit var mContext : Context

    // Place id brought from MapFragment
    private var place_id: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param: String) = DetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM, param)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            place_id = it.getString(ARG_PARAM)
        }
        binding = DataBindingUtil.inflate<FragmentDetailBinding>(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )

        db = AppDatabase.getAppDatabase(mContext)
        placeDao = db?.placeDao()


        request_place(place_id.toString())

        binding.btnGuardar.setOnClickListener {
            GlobalScope.launch {
                var place = com.andygeek.mapsproject.database.Place(obj.result.place_id, obj.result.name, null, null, null, null, null)
                placeDao?.insertAll(place)
                var data = db?.placeDao()?.getAll()
                data?.forEach {
                    println(it)
                }
            }
        }
        return binding.root
    }

    // Request place from API Google Maps
    fun request_place(place_id: String) {
        val queue = Volley.newRequestQueue(context)
        var url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&key=AIzaSyAYuDoz5piHJyOJ996l344nzwhdAcYM2Wg"
        var stringRequest = StringRequest(Request.Method.GET, url, { response ->
            println(response)
            var gson = Gson()
            obj = gson.fromJson(response, Place::class.java)
            println(obj.result.photos)
            binding.txtId.text = obj.result.name
            binding.txtAddress.text = obj.result.formatted_address
            var url : String? = null

            if(obj.result.rating == null){
                binding.rating.visibility = View.INVISIBLE
            }else{
                binding.rating.text = "Rating: ${obj.result.rating.toString()}"
            }

            if (obj.result.reviews != null){

                val recipeList : MutableList<Recipe>
                recipeList = arrayListOf()
                var num = 1
                var reviews = obj.result.reviews.size
                if(reviews >= 5){
                    for (i in 0..4){
                        num++
                        if(obj.result.reviews[i] != null){
                            println(obj.result.reviews[i].profile_photo_url)
                            val recipe = Recipe(obj.result.reviews[i].author_name, obj.result.reviews[i].text, obj.result.reviews[i].profile_photo_url)
                            recipeList.add(recipe)
                        }
                    }
                }
                else{
                    for (i in 0..reviews-1){
                        num++
                        if(obj.result.reviews[i] != null){
                            println(obj.result.reviews[i].profile_photo_url)
                            val recipe = Recipe(obj.result.reviews[i].author_name, obj.result.reviews[i].text, obj.result.reviews[i].profile_photo_url)
                            recipeList.add(recipe)
                        }
                    }
                }
                val adapter = RecipeAdapter(mContext, recipeList)
                binding.listViewReviews.adapter = adapter
            }
            if(obj.result.photos == null){
                println("No hay foto")
                Glide.with(this).load(R.drawable.sin_imagen).centerCrop().placeholder(R.drawable.loading).into(binding.imgReference)
            }else{
                println("Awqui hay foto")
                var first_photo = obj.result.photos.first()
                var reference = first_photo.photo_reference
                url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=$reference&key=AIzaSyAYuDoz5piHJyOJ996l344nzwhdAcYM2Wg"
                Glide.with(this).load(url).centerCrop().placeholder(R.drawable.loading).into(binding.imgReference)
            }
        }, {
            println("Error")
        })
        queue.add(stringRequest)
    }
}