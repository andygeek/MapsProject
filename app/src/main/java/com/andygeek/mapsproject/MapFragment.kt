package com.andygeek.mapsproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.andygeek.mapsproject.data.MyDataEntity
import com.andygeek.mapsproject.data.Point
import com.andygeek.mapsproject.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var message : Toast
    private lateinit var mContext : Context

    // Latitude and longitude initial
    private var latitude: String = "-12.085676267849536"
    private var longitude: String = "-77.02613957226276"

    // List of Points
    private lateinit var points: MutableList<Point>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        points = mutableListOf()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        @JvmStatic
        fun newInstance(param: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM, param)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater,
            R.layout.fragment_map,
            container,
            false
        )

        binding.findLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(binding.findLocation.query.toString() == ""){
                    message = Toast.makeText(context,getString(R.string.message_lack_keyword),Toast.LENGTH_SHORT)
                    message.show()
                }else if (latitude == "" || longitude == ""){
                    message = Toast.makeText(context,getString(R.string.message_lack_latitude_longitude),Toast.LENGTH_SHORT)
                    message.show()
                }else{
                    requestGooglemaps(latitude, longitude, binding.findLocation.query.toString())
                    latitude = ""
                    longitude = ""
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.btnSearch.setOnClickListener {
            if(binding.findLocation.query.toString() == ""){
                message = Toast.makeText(context,getString(R.string.message_lack_keyword),Toast.LENGTH_SHORT)
                message.show()
            }else if (latitude == "" || longitude == ""){
                message = Toast.makeText(context,getString(R.string.message_lack_latitude_longitude),Toast.LENGTH_SHORT)
                message.show()
            }else{
                requestGooglemaps(latitude, longitude, binding.findLocation.query.toString())
                latitude = ""
                longitude = ""
            }
        }

        binding.btnList.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapFragment_to_listFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    // State of map when the app initilize
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initialPosition()
        mMap.setOnMapClickListener { lt: LatLng ->
            clickPoint(lt)
        }
        mMap.setOnMarkerClickListener { marker: Marker ->
            clickMarker(marker)
        }
    }

    // Request to Google Maps
    private fun requestGooglemaps(latitude: String, longitude: String, keyword: String) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${getString(R.string.api_key_google)}&location=$latitude,$longitude&keyword=$keyword&radius=2000"
        val stringRequest =
            StringRequest(Request.Method.GET, url, { response ->
                addMarkers(response)
            }, {
                println("Error")
            })
        queue.add(stringRequest)
    }

    // Open detail fragment
    private fun openDetail(param: String) {
        val detailFragment = DetailFragment.newInstance(param)
        detailFragment.show(activity?.supportFragmentManager!!, "Tag")
    }

    // Click in point
    private fun clickPoint(lt: LatLng) {
        mMap.clear()
        val point = MarkerOptions().position(lt).title(getString(R.string.here))
        latitude = lt.latitude.toString()
        longitude = lt.longitude.toString()
        mMap.addMarker(point)
    }

    // Click in marker
    private fun clickMarker(marker: Marker): Boolean {
        if (marker.isInfoWindowShown) {
            marker.hideInfoWindow()
        } else {
            marker.showInfoWindow()
            val pointSend = points.find { p ->
                marker.title.toString() == p.name
            }
            if(pointSend != null){
                openDetail(pointSend?.id_place.toString())
            }
        }
        return true
    }

    // Initial position
    private fun initialPosition() {
        val lima = LatLng(-12.085676267849536, -77.02613957226276)
        mMap.addMarker(MarkerOptions().position(lima).title("Here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 10f))
    }

    // Add markers with response
    private fun addMarkers(response: String) {
        val gson = Gson()
        val obj = gson.fromJson(response, MyDataEntity::class.java)
        mMap.clear()
        points.clear()
        obj.results.forEach {
            val temporalPoint =
                Point(it.place_id, it.name, it.geometry.location.lat, it.geometry.location.lng)
            points.add(temporalPoint)
        }
        points.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            val point = MarkerOptions().position(latLng).title(it.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_1))
            mMap.addMarker(point)
        }

    }

}