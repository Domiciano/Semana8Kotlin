package edu.co.icesi.semana8kotlina

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import edu.co.icesi.semana8kotlina.databinding.ActivityMapsBinding
import kotlinx.android.synthetic.main.activity_maps.*
import java.text.DecimalFormat
import kotlin.math.min

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var user: String? = null

    private lateinit var manager: LocationManager

    private var me: Marker? = null

    private val points = ArrayList<Marker>()
    private lateinit var icesi: Polygon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.extras?.getString("user")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        manager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setInitialPos()
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2F, this)

        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)

        addBtn.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me?.position, 16F))
        }

        icesi = mMap.addPolygon(
            PolygonOptions()
                .add(LatLng(3.3431609893786156, -76.53088174760342))
                .add(LatLng(3.3434572037403165, -76.52729261666536))
                .add(LatLng(3.3407390570728444, -76.52734693139791))
                .add(LatLng(3.3407390570728444, -76.52828335762024))
                .add(LatLng(3.3386976807988327, -76.52825988829137))
                .add(LatLng(3.3386976807988327, -76.52974247932434))
                .add(LatLng(3.3398822083912405, -76.52986720204353))
                .add(LatLng(3.3398822083912405, -76.53117813169956))
                .add(LatLng(3.3431609893786156, -76.53088174760342))
                .fillColor(Color.argb(50, 255, 0, 0))
                .strokeColor(Color.BLACK)
        )
    }

    @SuppressLint("MissingPermission")
    private fun setInitialPos() {
        val location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
            updateMyLocation(location)
        }
    }

    override fun onLocationChanged(location: Location) {
        updateMyLocation(location)

        val imAtIcesi = PolyUtil.containsLocation(
            LatLng(location.latitude, location.longitude),
            icesi.points,
            true
        )
        if (imAtIcesi) {
            addBtn.text = "Esta en Icesi"
        }
    }

    fun updateMyLocation(location: Location) {
        val pos = LatLng(location.latitude, location.longitude)
        if (me == null) {
            me = mMap.addMarker(MarkerOptions().position(pos).title("Yo"))
        } else {
            me?.position = pos
        }
        //Esto se comenta, para que la cámara no se centre en el usuario de nuevo
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12F))

        computeDistance()
    }

    private fun computeDistance() {
        for (marker in points){
            val markerloc = marker.position
            val meloc = me?.position

            val meters = SphericalUtil.computeDistanceBetween(markerloc, meloc)
            Log.e(">>>", "Metros a marcador ${points.indexOf(marker)}: ${meters}M")

            if(meters<50){
                addBtn.text = "Usted está pisando un marcador"
            }
        }

        if(this::icesi.isInitialized) {
            var distanceToIcesi = 100000.0
            for(point in icesi.points){
                val meters = SphericalUtil.computeDistanceBetween(point, me?.position)
                distanceToIcesi = min(meters, distanceToIcesi)
            }
            mapTitle.text = "Distancia a Icesi: ${DecimalFormat("#.##").format(distanceToIcesi)}M"
        }
    }

    override fun onMapClick(latlng: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16F))
    }

    override fun onMapLongClick(latlng: LatLng) {
        val marker = mMap.addMarker(
            MarkerOptions().position(latlng).title("Marcador")
                .snippet("Este es un marcador de prueba")
        )
        points.add(marker)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(
            this,
            "${marker.position.latitude}, ${marker.position.longitude}",
            Toast.LENGTH_LONG
        ).show()
        Log.e(">>>", "${marker.position.latitude}, ${marker.position.longitude}")
        marker.showInfoWindow()
        return true
    }
}