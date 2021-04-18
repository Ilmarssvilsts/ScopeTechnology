package com.example.maphw.data.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.maphw.data.models.Vehicle
import com.example.maphw.data.repositories.VehicleRepository
import kotlinx.coroutines.launch

class VehicleViewModel(private val repository: VehicleRepository) : ViewModel() {
    val allVehicles: LiveData<List<Vehicle>> = repository.allVehicles.asLiveData()

    fun insert(vehicle: Vehicle) = viewModelScope.launch {
        repository.insert(vehicle)
    }
}

class VehicleViewModelFactory(private val repository: VehicleRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}