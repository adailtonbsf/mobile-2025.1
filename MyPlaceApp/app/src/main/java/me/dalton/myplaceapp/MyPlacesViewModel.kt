package me.dalton.myplaceapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MyPlacesViewModel: ViewModel() {

    var myPlaces = mutableStateListOf<MyPlace>()
        private set

    var selectedPlaces = mutableStateListOf<MyPlace>()
        private set

    fun addPlace(place: MyPlace) {
        myPlaces.add(place)
    }

    fun toggleSelected(place: MyPlace) {
        if (selectedPlaces.contains(place)) {
            selectedPlaces.remove(place)
        } else {
            if(selectedPlaces.size < 2) {
                selectedPlaces.add(place)
            } else {
                selectedPlaces.clear()
                selectedPlaces.add(place)
            }
        }
    }
}