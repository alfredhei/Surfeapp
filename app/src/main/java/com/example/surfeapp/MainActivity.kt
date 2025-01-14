package com.example.surfeapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu

import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.surfeapp.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener,
    OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    lateinit var list: Array<*>
    lateinit var spots: Spots

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo_5)
        }

        drawerLayout = findViewById(R.id.drawerLayout)

        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        actionBarToggle.syncState()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnInfoWindowClickListener(this)

        mMap.setMinZoomPreference(6.0f)
        val starterScope = LatLng(61.140304, 8.542487)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(starterScope))

        val adelaideBounds = LatLngBounds(
            LatLng(57.456008, -9.808621),  // SW grenser
            LatLng(80.291513, 33.990307) // NE grenser
        )
        mMap.setLatLngBoundsForCameraTarget(adelaideBounds)
        val viewModel1: MainActivityViewModel by viewModels()

        viewModel1.getSurfespotsMain().observe(this) {
            var i = 0
            val size: Int = it.list.size
            spots = it

            while (i < size) {
                val longMain: Double = it.list[i].coordinates.longitude
                val latMain: Double = it.list[i].coordinates.latitude
                val nameCords: String = it.list[i].name
                val temp = LatLng(latMain, longMain)
                mMap.addMarker(MarkerOptions().position(temp).title(nameCords).snippet("Trykk for å se mer").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.pin)))
                i++
            }

        }

        viewModel1.fetchSurfespotsMain(applicationContext)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            this.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    fun tilOmSiden(item: android.view.MenuItem) {
        val intent = Intent(this, OmSidenActivity::class.java)
        startActivity(intent)
    }
    fun tilNybegynner(item: android.view.MenuItem) {
        val intent = Intent(this, NybegynnerActivity::class.java)
        startActivity(intent)
    }
    fun tilTipsOgTriks(item: android.view.MenuItem) {
        val intent = Intent(this, TipsOgTriksActivity::class.java)
        startActivity(intent)
    }

    fun tilTop5(item: android.view.MenuItem) {
        val intent = Intent(this, Topp5Activity::class.java)
        startActivity(intent)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onInfoWindowClick(p0: Marker) {
            val intent = Intent(this, SpotActivity::class.java)
            intent.putExtra("spotTitle", p0.title)
            startActivity(intent)        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val searchManager = menu.findItem(R.id.search).actionView as SearchView
        searchManager.isIconified = true
        searchManager.isIconifiedByDefault = true

        searchManager.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(baseContext, SearchResultsActivity::class.java)
                intent.putExtra("spotList", spots.list.map {it.name}.toTypedArray())
                intent.putExtra("query", query)
                searchManager.setQuery("", false)
                searchManager.isIconified = true
                startActivity(intent)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.equals("")){
                    val intent = Intent(baseContext, SearchResultsActivity::class.java)
                    intent.putExtra("spotList", spots.list.map {it.name}.toTypedArray())
                    intent.putExtra("query", newText)
                    searchManager.setQuery("", false)
                    searchManager.isIconified = true
                    startActivity(intent)
                }
                return true
            }
        })
        return true
    }

}