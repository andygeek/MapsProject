package com.andygeek.mapsproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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
import java.io.ByteArrayOutputStream
import java.net.URL

// Key for param
private const val ARG_PARAM = "PLACE_ID"

class DetailFragment : BottomSheetDialogFragment() {

    private var db: AppDatabase? = null
    private var placeDao: PlaceDao? = null
    private lateinit var mContext : Context
    private lateinit var binding: FragmentDetailBinding
    private lateinit var obj : Place
    private lateinit var message : Toast

    // Place id brought from MapFragment
    private var placeId: String? = null

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
            placeId = it.getString(ARG_PARAM)
        }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )

        // Request for the information of place
        requestPlace(placeId.toString())

        // Initialize database
        db = AppDatabase.getAppDatabase(mContext)
        placeDao = db?.placeDao()

        // Save Buton
        binding.btnSave.setOnClickListener {
            GlobalScope.launch {
                val place = com.andygeek.mapsproject.database.Place(obj.result.place_id, obj.result.name, obj.result.formatted_address, encodeImage(getFirstPhoto(obj)), null, null, null)
                placeDao?.insertAll(place)
            }
            message = Toast.makeText(context,getString(R.string.message_save),Toast.LENGTH_SHORT)
            message.show()
        }

        return binding.root
    }

    // Request place from API Google Maps
    private fun requestPlace(place_id: String) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&key=${getString(R.string.api_key_google)}"
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            obj = gson.fromJson(response, Place::class.java)

            // Show the some variables
            binding.txtId.text = obj.result.name
            binding.txtAddress.text = obj.result.formatted_address
            val ratingOfPlace = "Rating: ${obj.result.rating}"
            binding.rating.text = ratingOfPlace

            // Show the first image of place
            if(obj.result.photos != null){
                val firstPhoto = obj.result.photos.first()
                val photoReferenceKey = firstPhoto.photo_reference
                val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=$photoReferenceKey&key=${getString(R.string.api_key_google)}"
                Glide.with(this).load(photoUrl).centerCrop().placeholder(R.drawable.loading).into(binding.imgReference)
            }
            else{
                Glide.with(this).load(R.drawable.no_image).centerCrop().placeholder(R.drawable.loading).into(binding.imgReference)
            }

            if(obj.result.reviews != null){
                // Variables for ListView that required Recipe and RecipeAdapter
                val reviewerList : MutableList<Recipe>
                reviewerList = arrayListOf()

                // For the reviewers are older than 5
                val reviews = obj.result.reviews.size
                if(reviews >= 5){
                    for (i in 0..4){
                        val recipe = Recipe(obj.result.reviews[i].author_name, obj.result.reviews[i].text, obj.result.reviews[i].profile_photo_url)
                        reviewerList.add(recipe)
                    }
                }
                else{
                    for (i in 0 until reviews){
                        val recipe = Recipe(obj.result.reviews[i].author_name, obj.result.reviews[i].text, obj.result.reviews[i].profile_photo_url)
                        reviewerList.add(recipe)
                    }
                }
                val adapter = RecipeAdapter(mContext, reviewerList)
                binding.listViewReviews.adapter = adapter
            }

        }, {
            println("Error")
        })
        queue.add(stringRequest)
    }

    private fun encodeImage(image : String): String{
        if(image == ""){
            val baos = ByteArrayOutputStream()
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.no_image )
            val b = baos.toByteArray()
            val encodeImage = Base64.encodeToString(b, Base64.DEFAULT)
            return encodeImage
        }else{
            val url = URL(image)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val encodeImage = Base64.encodeToString(b, Base64.DEFAULT)
            return encodeImage
        }
    }

    private fun getFirstPhoto(place : Place):String{
        if(place.result.photos == null){
            return ""
        }else{
            val firstPhoto = place.result.photos.first()
            val photoReferenceKey = firstPhoto.photo_reference
            val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=$photoReferenceKey&key=${getString(R.string.api_key_google)}"
            return photoUrl
        }

    }
}