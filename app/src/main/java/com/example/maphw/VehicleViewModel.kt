package com.example.maphw

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.maphw.data.Owner
import com.example.maphw.data.Vehicle
import kotlinx.coroutines.launch

class VehicleViewModel(private val repository: VehicleRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    val allVehicles: LiveData<List<Vehicle>> = repository.allVehicles.asLiveData()


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(vehicle: Vehicle) = viewModelScope.launch {
        repository.insert(vehicle)
    }
}

class VehicleViewModelFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}