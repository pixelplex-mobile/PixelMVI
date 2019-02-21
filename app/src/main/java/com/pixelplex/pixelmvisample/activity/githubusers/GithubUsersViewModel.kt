package com.pixelplex.pixelmvisample.activity.githubusers

import com.pixelplex.pixelmvi.viewmodel.ReducerSupportStreamViewModel
import com.pixelplex.pixelmvi.viewmodel.UiEvent
import com.pixelplex.pixelmvisample.model.RemoteGithubUser
import com.pixelplex.pixelmvisample.service.GithubApiService

class GithubUsersViewModel(private val githubApiService: GithubApiService) :
    ReducerSupportStreamViewModel<GithubUsersUiEvent, GithubUsersState, GithubUsersResult>(
        GithubUsersState()
    ) {

    init {
        query(GithubUsersUiEvent.RequestNewUsersBatch::class.java) { event ->
            GithubUsersResult.UsersBatch(
                githubApiService.getUsersBatchAsync(
                    lastState().users.lastOrNull()?.id ?: 0
                ).await()
            )
        }
        reducer(GithubUsersResult.UsersBatch::class.java) { currentState, result ->
            currentState.copy(
                users = result.users
            )
        }
    }
}

sealed class GithubUsersUiEvent : UiEvent {
    object RequestNewUsersBatch : GithubUsersUiEvent()
}

data class GithubUsersState(
    val users: List<RemoteGithubUser> = emptyList()
)

sealed class GithubUsersResult {
    data class UsersBatch(val users: List<RemoteGithubUser>) : GithubUsersResult()
}