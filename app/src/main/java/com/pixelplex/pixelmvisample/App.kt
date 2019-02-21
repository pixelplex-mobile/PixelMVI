package com.pixelplex.pixelmvisample

import android.app.Application
import com.pixelplex.pixelmvisample.activity.githubuseroverview.GithubUserOverviewViewModel
import com.pixelplex.pixelmvisample.activity.githubusers.GithubUsersViewModel
import com.pixelplex.pixelmvisample.service.GithubApiService
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(usersModule, userOverviewModule)
        )
    }

    private val usersModule: Module = module {
        viewModel { GithubUsersViewModel(get()) }
        single { GithubApiService.instantiate() }
    }

    private val userOverviewModule: Module = module {
        viewModel { (userLogin: String) -> GithubUserOverviewViewModel(get(), userLogin) }
    }
}