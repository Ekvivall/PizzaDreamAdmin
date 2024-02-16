package com.sokol.pizzadreamadmin.ui.pizzerias

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sokol.pizzadreamadmin.Adapter.PizzeriaAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.R

class PizzeriasFragment : Fragment() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var mapView: MapView
    private lateinit var pizzeriasViewModel: PizzeriasViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var pizzeriaRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val pizzeriasViewModel = ViewModelProvider(this).get(PizzeriasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_pizzerias, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            initView(root)
            initMap(savedInstanceState)
            pizzeriasViewModel.getPizzeriasMutableLiveData()
                .observe(viewLifecycleOwner) { addressList ->
                    val adapter = PizzeriaAdapter(addressList, requireContext())
                    pizzeriaRecycler.adapter = adapter
                    updateMapMarkers(addressList)

                }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun initView(root: View) {
        radioGroup = root.findViewById(R.id.radioGroup)
        mapView = root.findViewById(R.id.map_view)
        pizzeriaRecycler = root.findViewById(R.id.pizzeria_recycler)
        pizzeriaRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        pizzeriaRecycler.layoutManager = layoutManager
        pizzeriaRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(), layoutManager.orientation
            )
        )
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonMap -> {
                    mapView.visibility = View.VISIBLE
                    pizzeriaRecycler.visibility = View.GONE
                }

                R.id.radioButtonList -> {
                    mapView.visibility = View.GONE
                    pizzeriaRecycler.visibility = View.VISIBLE
                }
            }
        }
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    private fun updateMapMarkers(addressList: List<PizzeriaModel>) {
        googleMap.clear()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    48.4652948442045,
                    35.05258267832949
                ), 12.0f
            )
        )
        for (address in addressList) {
            val markerOptions = MarkerOptions()
                .position(LatLng(address.lat, address.lng))
                .title(address.name)
                .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_local_pizza_24))
            googleMap.addMarker(markerOptions)
        }
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorResId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}