package com.example.maphw

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.example.maphw.data.Owner
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
        OwnerViewModelFactory((application.getApplicationContext() as MapApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecyclerView = findViewById(R.id.usersList) as RecyclerView
        userRecyclerView!!.layoutManager = LinearLayoutManager(applicationContext)
        userRecyclerView?.adapter =
                UsersAdapter(arrayListOf(), R.layout.item_user, applicationContext, this)
        noData = findViewById(R.id.no_data) as TextView
        progress = findViewById(R.id.progress) as ProgressBar
        swipeRefreshLayout = findViewById(R.id.refresh) as SwipeRefreshLayout

        userViewModel.allWords.observe(owner = this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {
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
        if((getTimeInMilli() - oneDay) >= PreferenceManager.getDefaultSharedPreferences(applicationContext).getLong(
                        "LastUpdateDate", 0)){
            getData()
        }
    }



    private fun onFailure(t: Throwable) {
        if (usersList.size == 0) {
            progress?.visibility = View.GONE
            noData?.visibility = View.VISIBLE
        }
        swipeRefreshLayout?.setRefreshing(false)
        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
    }

    private fun onResponse(response: UserList) {
        progress?.visibility = View.GONE
        noData?.visibility = View.GONE
        usersList = response.data as MutableList<User>

        cleanList(usersList)
        addToDataBase(ownersList)

        (userRecyclerView?.adapter as UsersAdapter).updateUsers(ownersList)
        swipeRefreshLayout?.setRefreshing(false)
        //todo when database is added, this should be checked and list updated every day
        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                .putLong("LastUpdateDate", getTimeInMilli()).apply()
    }

    override fun onItemClicked(position: Int) {
        val intent = Intent(applicationContext, MapActivity::class.java)

        intent.putExtra("id", usersList[position].userid.toString())
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
            }
            else{
                ownersList.add(Owner(this.usersList[i].userid!!, this.usersList[i].owner?.name!!, this.usersList[i].owner?.surname!!, this.usersList[i].owner?.foto!!))
            }
        }
    }

    private fun getData() {
        if (isNetworkConnected()) {
            API.buildApi().getUserList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        } else {
            Toast.makeText(applicationContext, getString(R.string.no_network), Toast.LENGTH_LONG).show()
        }
    }

    private fun getTimeInMilli(): Long {
        val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val localDate: LocalDateTime =
                LocalDateTime.parse(Calendar.getInstance().time.toString(), formatter)
        val timeInMilliseconds: Long = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        return timeInMilliseconds
    }

    //todo should change methods and not use deprecated ones.
    //todo add this to utils class
    private fun isNetworkConnected(): Boolean {
        val cm = applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}