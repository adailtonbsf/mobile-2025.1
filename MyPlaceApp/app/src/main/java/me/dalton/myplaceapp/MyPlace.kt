package me.dalton.myplaceapp

import com.google.android.gms.maps.model.LatLng

data class MyPlace(
    val name: String,
    val latLng: LatLng
)