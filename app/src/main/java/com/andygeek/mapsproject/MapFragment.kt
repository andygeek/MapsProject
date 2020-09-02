package com.andygeek.mapsproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.Response
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

private const val ARG_PARAM = "PLACE_ID"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var latitude: String
    private lateinit var longitude: String

    private lateinit var points : MutableList<Point>

    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM)
        }
        points = mutableListOf()
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
                request_googlemaps(latitude, longitude, binding.findLocation.query.toString())
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.btnTest.setOnClickListener {
            //var bottom = DetailFragment.newInstance("Andy")
            //bottom.show(activity?.supportFragmentManager!!, "Tag")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM, param1)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initial_position()
        mMap.setOnMapClickListener { lt: LatLng ->
            click_point(lt)
        }
        mMap.setOnMarkerClickListener { marker: Marker ->
            click_marker(marker)
        }
    }

    // Request to Google Maps
    fun request_googlemaps(latitude: String, longitude: String, keyword: String) {
        val queue = Volley.newRequestQueue(context)
        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAYuDoz5piHJyOJ996l344nzwhdAcYM2Wg&location=$latitude,$longitude&keyword=$keyword&radius=2000"
        val stringRequest =
            StringRequest(Request.Method.GET, url, { response ->
                add_markers(response)
            }, {
                println("Error")
            })
        queue.add(stringRequest)
    }

    // Open detail fragment
    fun open_detail(param : String){
        var detailFragment = DetailFragment.newInstance(param)
        detailFragment.show(activity?.supportFragmentManager!!, "Tag")
    }

    // Click in point
    fun click_point(lt: LatLng){
        mMap.clear()
        var point = MarkerOptions().position(lt).title("Here")
        mMap.addMarker(point)
        latitude = lt.latitude.toString()
        longitude = lt.longitude.toString()
    }

    // Click in marker
    fun click_marker(marker : Marker):Boolean{
        if (marker.isInfoWindowShown) {
            marker.hideInfoWindow()

        } else {
            marker.showInfoWindow()
            var point_send = points.find { p -> marker.title.toString().equals(p.name) }
            open_detail(point_send?.id_place.toString())
        }
        return true
    }

    // Initial position
    fun initial_position(){
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    // Add markers with response
    fun add_markers(response : String){
        var gson = Gson()
        var obj = gson?.fromJson(response, MyDataEntity::class.java)
        mMap.clear()
        points.clear()
        obj.results.forEach {
            var temporal_point = Point(it.place_id, it.name, it.geometry.location.lat,it.geometry.location.lng )
            points.add(temporal_point)
        }
        points.forEach {
            var latLng = LatLng(it.latitude, it.longitude)
            var point = MarkerOptions().position(latLng).title(it.name).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_1))
            mMap.addMarker(point)
        }

    }
}