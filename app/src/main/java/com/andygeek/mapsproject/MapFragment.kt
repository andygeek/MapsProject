package com.andygeek.mapsproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

// Key for param to show DetailFragment
private const val ARG_PARAM = "PLACE_ID"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // Latitude and longitude initial
    private var latitude: String = "-12.0717281697085"
    private var longitude: String = "-75.20610196970848"

    // List of Points
    private lateinit var points: MutableList<Point>

    // Param from Map
    //private var param: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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

        binding.btnSearch.setOnClickListener {
            request_googlemaps(latitude, longitude, binding.findLocation.query.toString())
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

    companion object {
        fun newInstance(param: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM, param)
                }
            }
    }

    // State of map when the app initilize
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
    private fun request_googlemaps(latitude: String, longitude: String, keyword: String) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${getString(R.string.api_key_google)}&location=$latitude,$longitude&keyword=$keyword&radius=2000"
        val stringRequest =
            StringRequest(Request.Method.GET, url, { response ->
                add_markers(response)
            }, {
                println("Error")
            })
        queue.add(stringRequest)
    }

    // Open detail fragment
    private fun open_detail(param: String) {
        val detailFragment = DetailFragment.newInstance(param)
        detailFragment.show(activity?.supportFragmentManager!!, "Tag")
    }

    // Click in point
    private fun click_point(lt: LatLng) {
        mMap.clear()
        val point = MarkerOptions().position(lt).title(getString(R.string.here))
        mMap.addMarker(point)
        latitude = lt.latitude.toString()
        longitude = lt.longitude.toString()
    }

    // Click in marker
    private fun click_marker(marker: Marker): Boolean {
        if (marker.isInfoWindowShown) {
            marker.hideInfoWindow()

        } else {
            marker.showInfoWindow()
            val point_send = points.find { p -> marker.title.toString().equals(p.name) }
            //println("Enviaremos este codigo: ${point_send?.id_place}")
            open_detail(point_send?.id_place.toString())
        }
        return true
    }

    // Initial position
    private fun initial_position() {
        val sydney = LatLng(-12.0717281697085, -75.20610196970848)
        mMap.addMarker(MarkerOptions().position(sydney).title("Here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }

    // ------------------------------------------------------------ HERE ---------------------------------------------------------------------



    // Add markers with response
    private fun add_markers(response: String) {
        val gson = Gson()
        val obj = gson.fromJson(response, MyDataEntity::class.java)
        mMap.clear()
        points.clear()
        obj.results.forEach {
            val temporal_point =
                Point(it.place_id, it.name, it.geometry.location.lat, it.geometry.location.lng)
            points.add(temporal_point)
        }
        points.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            val point = MarkerOptions().position(latLng).title(it.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_1))
            mMap.addMarker(point)
        }

    }

    private fun search() {

    }
}