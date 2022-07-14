package com.mariosodigie.apps.spacexdemoapp.launches.ui

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariosodigie.apps.spacexdemoapp.R
import com.mariosodigie.apps.spacexdemoapp.databinding.ActivityLaunchListBinding
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.NetworkError
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchListActivity : AppCompatActivity(),OnNetworkErrorListener {

    private val viewModel: LaunchListViewModel by viewModel()
    private lateinit var binding: ActivityLaunchListBinding

    private lateinit var adapter: LaunchListAdapter

    override var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchListBinding.inflate(layoutInflater)

        with(binding){
            setContentView(root)
            setSupportActionBar(myToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            adapter = LaunchListAdapter { TODO() }

            launchList.layoutManager = LinearLayoutManager(this@LaunchListActivity)
            val decorator = DividerItemDecoration(this@LaunchListActivity,DividerItemDecoration.VERTICAL)
            decorator.setDrawable(ContextCompat.getDrawable(this@LaunchListActivity, R.drawable.recycler_decorator)!!)
            launchList.addItemDecoration(decorator)
            launchList.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getLaunchList(forceRefresh = true)
            }
        }

        viewModel.launchTitleLiveData.observe(this){
            binding.myToolbar.title = getString(R.string.launch_toolbar_title, it)
        }

        viewModel.launchDetailsLiveData.observe(this){
            adapter.submitList(it)
        }

        viewModel.requestInProgress.observe(this) {
            binding.requestProgress.apply {
                visibility = if(it) View.VISIBLE else View.GONE
            }
        }
        addErrorSource(viewModel.networkError)
        fillActivity()
    }

    private fun fillActivity() = viewModel.getLaunchList()

    override fun addErrorSource(source: LiveData<NetworkError>) {
        source.observe(this, ::showErrorDialog)
    }

    override fun showErrorDialog(error: NetworkError) {
        alertDialog = AlertDialog.Builder(this)
            .apply {
                setTitle(error.title)
            }
            .setMessage(error.message)
            .setPositiveButton(R.string.dialog_ok){ dialog, _->
                dialog.dismiss()
            }
            .setOnCancelListener { dialog->
                dialog.dismiss()
            }
            .setOnDismissListener{
                alertDialog = null
            }
            .apply {  error.icon?.let { this.setIcon(it) } }
            .show()
    }
}