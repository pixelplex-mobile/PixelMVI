package com.pixelplex.pixelmvisample.activity.githubuseroverview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pixelplex.pixelmvi.tools.StateObserver
import com.pixelplex.pixelmvisample.R
import kotlinx.android.synthetic.main.activity_user_overview.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GithubUserOverviewActivity : AppCompatActivity(), StateObserver<GithubUserOverviewState> {

    private val viewModel by viewModel<GithubUserOverviewViewModel> {
        parametersOf(
            intent.getStringExtra(USER_LOGIN)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_overview)

        with(viewModel) {
            uiState.observe(this@GithubUserOverviewActivity, this@GithubUserOverviewActivity)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onChanged(t: GithubUserOverviewState) {
        if (t.user.login.isNotEmpty()) {
            progress_bar.visibility = GONE
            tv_title.text = t.user.login
            Glide.with(this)
                .load(t.user.avatarUrl)
                .into(iv_avatar)
            tv_web_url.text = t.user.webUrl
            tv_repos_url.text = t.user.reposUrl
            ll_account.visibility = VISIBLE
            ll_repos.visibility = VISIBLE
        }
    }

    companion object {
        const val USER_LOGIN = "user_login"
        fun start(context: Context, userLogin: String) {
            context.startActivity(Intent(context, GithubUserOverviewActivity::class.java).apply {
                putExtra(USER_LOGIN, userLogin)
            })
        }
    }
}