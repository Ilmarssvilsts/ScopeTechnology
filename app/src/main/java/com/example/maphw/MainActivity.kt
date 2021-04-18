package com.example.maphw

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.maphw.activities.MapActivity
import com.example.maphw.adapters.UsersAdapter
import com.example.maphw.api.API
import com.example.maphw.api.models.User
import com.example.maphw.api.models.UserList
import com.example.maphw.data.models.Owner
import com.example.maphw.data.models.Vehicle
import com.example.maphw.data.viewModels.OwnerViewModelFactory
import com.example.maphw.data.viewModels.UserViewModel
import com.example.maphw.data.viewModels.VehicleViewModel
import com.example.maphw.data.viewModels.VehicleViewModelFactory
import com.example.maphw.utils.Utils.isNetworkConnected
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity(), UsersAdapter.OnItemClickListener {

    private var userRecyclerView: RecyclerView? = null
    private var usersList: MutableList<User> = mutableListOf()
    private var ownersList: MutableList<Owner> = mutableListOf()
    private var noData: TextView? = null
    private var progress: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val userViewModel: UserViewModel by viewModels {
        OwnerViewModelFactory((applicationContext as MapApplication).ownerRepository)
    }
    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory((applicationContext as MapApplication).vehicleRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecyclerView = findViewById(R.id.usersList)
        userRecyclerView!!.layoutManager = LinearLayoutManager(applicationContext)
        userRecyclerView?.adapter =
            UsersAdapter(arrayListOf(), R.layout.item_user, applicationContext, this)
        noData = findViewById(R.id.no_data)
        progress = findViewById(R.id.progress)
        swipeRefreshLayout = findViewById(R.id.refresh)

        userViewModel.allOwners.observe(owner = this) { item ->
            item.let {
                progress?.visibility = View.GONE
                noData?.visibility = View.GONE
                ownersList = it as MutableList<Owner>
                (userRecyclerView?.adapter as UsersAdapter).updateUsers(ownersList)
            }
        }

        swipeRefreshLayout?.setOnRefreshListener {
            getData()
        }
    }

    override fun onStart() {
        super.onStart()

        val oneDay = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        if ((getTimeInMilli() - oneDay) >= PreferenceManager.getDefaultSharedPreferences(
                applicationContext
            ).getLong(
                "LastUpdateDate", 0
            )
        ) {
            getData()
        }
    }

    private fun onFailure(t: Throwable) {
        if (usersList.size == 0) {
            progress?.visibility = View.GONE
            noData?.visibility = View.VISIBLE
        }
        swipeRefreshLayout?.isRefreshing = false
        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
    }

    private fun onResponse(response: UserList) {
        progress?.visibility = View.GONE
        noData?.visibility = View.GONE
        usersList = response.data as MutableList<User>

        cleanList(usersList)
        addToDataBase(ownersList)

        (userRecyclerView?.adapter as UsersAdapter).updateUsers(ownersList)
        swipeRefreshLayout?.isRefreshing = false

        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
            .putLong("LastUpdateDate", getTimeInMilli()).apply()
    }

    override fun onItemClicked(position: Int) {
        val intent = Intent(applicationContext, MapActivity::class.java)

        intent.putExtra("id", ownersList[position].userId)
        startActivity(intent)
    }

    private fun addToDataBase(owners: MutableList<Owner>) {
        for (i in owners) {
            userViewModel.insert(i)
        }
    }

    //List needs to be cleaned, because API returns last item as empty
    private fun cleanList(usersList: MutableList<User>) {
        ownersList.clear()
        for (i in usersList.indices) {
            if (usersList[i].userid == null) {
                this.usersList.removeAt(i)
            } else {
                ownersList.add(
                    Owner(
                        this.usersList[i].userid!!,
                        this.usersList[i].owner?.name!!,
                        this.usersList[i].owner?.surname!!,
                        this.usersList[i].owner?.photo!!
                    )
                )

                var vehicleList = this.usersList[i].vehicles
                if (vehicleList != null) {
                    for (j in vehicleList) {
                        var vehicle = Vehicle(
                            this.usersList[i].userid!!,
                            j.vehicleId!!,
                            j?.make!!,
                            j?.model!!,
                            j.year!!,
                            j?.color!!,
                            j?.vin!!,
                            j?.photo!!
                        )
                        vehicleViewModel.insert(vehicle)
                    }
                }
            }
        }
    }

    private fun getData() {
        if (isNetworkConnected(baseContext)) {
            API.buildApi().getUserList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        } else {
            Toast.makeText(applicationContext, getString(R.string.no_network), Toast.LENGTH_LONG)
                .show()
            swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun getTimeInMilli(): Long {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val localDate: LocalDateTime =
            LocalDateTime.parse(Calendar.getInstance().time.toString(), formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}