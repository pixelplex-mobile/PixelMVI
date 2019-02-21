package com.pixelplex.pixelmvisample.activity.githubuseroverview

import com.pixelplex.pixelmvi.viewmodel.ReducerSupportStreamViewModel
import com.pixelplex.pixelmvi.viewmodel.UiEvent
import com.pixelplex.pixelmvisample.model.RemoteGithubUser
import com.pixelplex.pixelmvisample.service.GithubApiService

class GithubUserOverviewViewModel(
    private val githubApiService: GithubApiService,
    private val userLogin: String
) :
    ReducerSupportStreamViewModel<GithubUserOverviewUiEvent, GithubUserOverviewState, GithubUserOverviewResult>(
        GithubUserOverviewState()
    ) {
    init {
        query(GithubUserOverviewUiEvent.LoadUserOverview::class.java) { event ->
            GithubUserOverviewResult.UserOverview(
                githubApiService.getUserOverviewAsync(userLogin).await()
            )
        }
        reducer(GithubUserOverviewResult.UserOverview::class.java) { currentState, result ->
            currentState.copy(
                user = result.user
            )
        }
        nextEvent(GithubUserOverviewUiEvent.LoadUserOverview)
    }
}

sealed class GithubUserOverviewUiEvent : UiEvent {
    object LoadUserOverview : GithubUserOverviewUiEvent()
}

data class GithubUserOverviewState(
    val user: RemoteGithubUser = RemoteGithubUser.empty()
)

sealed class GithubUserOverviewResult {
    data class UserOverview(val user: RemoteGithubUser) : GithubUserOverviewResult()
}