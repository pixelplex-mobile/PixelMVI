package com.pixelplex.pixelmvisample.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RemoteGithubUser(
    @Expose
    val login: String,

    @Expose
    val id: Int,

    @SerializedName("node_id")
    val nodeId: String,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("html_url")
    val webUrl: String,

    @SerializedName("repos_url")
    val reposUrl: String,

    @SerializedName("site_admin")
    val isAdmin: Boolean
) {
    companion object {
        fun empty(): RemoteGithubUser {
            return RemoteGithubUser("", 0, "", "", "", "", false)
        }
    }
}