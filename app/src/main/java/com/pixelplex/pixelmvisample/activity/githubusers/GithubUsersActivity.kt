package com.pixelplex.pixelmvisample.activity.githubusers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelplex.pixelmvi.tools.StateObserver
import com.pixelplex.pixelmvisample.R
import com.pixelplex.pixelmvisample.activity.githubuseroverview.GithubUserOverviewActivity
import kotlinx.android.synthetic.main.activity_github_users.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubUsersActivity : AppCompatActivity(), StateObserver<GithubUsersState> {

    private val viewModel by viewModel<GithubUsersViewModel>()

    private val usersAdapter = GithubUsersRecyclerViewAdapter(::proceedClick, ::loadMore)

    private fun loadMore() {
        viewModel.nextEvent(GithubUsersUiEvent.RequestNewUsersBatch)
    }

    private fun proceedClick(adapterPosition: Int) {
        GithubUserOverviewActivity.start(this, usersAdapter.getItem(adapterPosition).login)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_users)

        rv_users.layoutManager = LinearLayoutManager(this)
        rv_users.adapter = usersAdapter

        with(viewModel) {
            uiState.observe(this@GithubUsersActivity, this@GithubUsersActivity)
        }

        viewModel.nextEvent(GithubUsersUiEvent.RequestNewUsersBatch)
    }

    override fun onChanged(t: GithubUsersState) {
        if (t.users.isNotEmpty()) {
            usersAdapter.notifyBatch(t.users)
        }
    }
}
