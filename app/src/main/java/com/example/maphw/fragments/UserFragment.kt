package com.example.maphw.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.maphw.MapApplication
import com.example.maphw.OwnerViewModelFactory
import com.example.maphw.activities.MapActivity
import com.example.maphw.R
import com.example.maphw.adapters.UsersAdapter
import com.example.maphw.api.API
import com.example.maphw.api.models.User
import com.example.maphw.api.models.UserList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class UserFragment : Fragment(), UsersAdapter.OnItemClickListener {

    private var userRecyclerView: RecyclerView? = null
    private var usersList: MutableList<User> = mutableListOf()
    private var noData: TextView? = null
    private var progress: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        userRecyclerView = view.findViewById(R.id.usersList) as RecyclerView
        userRecyclerView!!.layoutManager = LinearLayoutManager(context)
        userRecyclerView?.adapter =
            UsersAdapter(arrayListOf(), R.layout.item_user, context, this)
        noData = view.findViewById(R.id.no_data) as TextView
        progress = view.findViewById(R.id.progress) as ProgressBar
        swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        swipeRefreshLayout?.setOnRefreshListener {
            getData()
        }
    }

    private fun onFailure(t: Throwable) {
        if (usersList.size == 0) {
            progress?.visibility = View.GONE
            noData?.visibility = View.VISIBLE
        }
        swipeRefreshLayout?.setRefreshing(false)
        Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
    }

    private fun onResponse(response: UserList) {
        progress?.visibility = View.GONE
        noData?.visibility = View.GONE
        usersList = response.data as MutableList<User>
        cleanList(usersList)
        (userRecyclerView?.adapter as UsersAdapter).updateUsers(usersList)
        swipeRefreshLayout?.setRefreshing(false)
        //todo when database is added, this should be checked and list updated every day
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putLong("LastUpdateDate", getTimeInMilli()).apply()
    }

    //List needs to be cleaned, because API returns last item as empty
    private fun cleanList(usersList: MutableList<User>) {
        for (i in usersList.indices) {
            if (usersList[i].userid == null) {
                this.usersList.removeAt(i)
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
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_LONG).show()
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

    override fun onItemClicked(position: Int) {
        val intent = Intent(context, MapActivity::class.java)

        intent.putExtra("id", usersList[position].userid.toString())
        startActivity(intent)
    }

    //todo should change methods and not use deprecated ones.
    //todo add this to utils class
    private fun isNetworkConnected(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}