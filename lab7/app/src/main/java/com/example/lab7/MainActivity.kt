package com.example.lab7

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var tvCoordinates: TextView
    private val requestCodeLocation = 100

    private lateinit var userLocationListener: UserLocationObjectListener
    private lateinit var mapTapListener: GeoObjectTapListener
    private lateinit var userArrowTapListener: MapObjectTapListener
    private lateinit var placemarkTapListener: MapObjectTapListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)
        tvCoordinates = findViewById(R.id.tvCoordinates)
        val map = mapView.mapWindow.map

        // 1. Слой текущего местоположения
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = false

        userLocationListener = object : UserLocationObjectListener {
            override fun onObjectAdded(userLocationView: UserLocationView) {
                val point = userLocationView.arrow.geometry
                map.move(
                    CameraPosition(point, 15.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )

                // Клик по синему кружку (моё местоположение)
                userArrowTapListener = object : MapObjectTapListener {
                    override fun onMapObjectTap(mapObject: MapObject, tapPoint: Point): Boolean {
                        showCoordinates(tapPoint, "Моё местоположение")
                        return true
                    }
                }
                userLocationView.arrow.addTapListener(WeakReference(userArrowTapListener))
            }
            override fun onObjectRemoved(userLocationView: UserLocationView) {}
            override fun onObjectUpdated(userLocationView: UserLocationView, event: ObjectEvent) {}
        }
        userLocationLayer.setObjectListener(WeakReference(userLocationListener))

        checkAndRequestLocationPermission()

        // 2. Создаём несколько меток (по методичке)
        addInitialPlacemarks(map)

        // 3. Клик по пустому месту карты — добавляем новую метку
        mapTapListener = object : GeoObjectTapListener {
            override fun onObjectTap(event: GeoObjectTapEvent): Boolean {
                val point: Point? = event.geoObject.geometry[0].point
                if (point != null) {
                    addPlacemark(map, point)
                    showCoordinates(point, "Новая метка")
                }
                return true
            }
        }
        map.addTapListener(WeakReference(mapTapListener))
    }

    private fun addPlacemark(map: com.yandex.mapkit.map.Map, point: Point) {
        // Если не указывать setIcon, Яндекс автоматически использует стандартную красную метку
        val placemark = map.mapObjects.addPlacemark().apply {
            geometry = point
        }

        val placemarkTapListener = object : MapObjectTapListener {
            override fun onMapObjectTap(mapObject: MapObject, tapPoint: Point): Boolean {
                showCoordinates(tapPoint, "Метка")
                return true
            }
        }
        placemark.addTapListener(WeakReference(placemarkTapListener))
    }

    // Несколько меток при запуске
    private fun addInitialPlacemarks(map: com.yandex.mapkit.map.Map) {
        addPlacemark(map, Point(55.751225, 37.62954)) // Москва
        addPlacemark(map, Point(59.934280, 30.335099)) // Санкт-Петербург
        addPlacemark(map, Point(55.030199, 82.920430)) // Новосибирск
    }

    // Вывод координат в TextView под картой
    private fun showCoordinates(point: Point, source: String) {
        tvCoordinates.text = "$source:\nШирота: ${point.latitude}\nДолгота: ${point.longitude}"
    }

    private fun checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeLocation
            )
        } else {
            enableMyLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeLocation) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Без разрешения геолокация недоступна", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableMyLocation() {
        userLocationLayer.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}