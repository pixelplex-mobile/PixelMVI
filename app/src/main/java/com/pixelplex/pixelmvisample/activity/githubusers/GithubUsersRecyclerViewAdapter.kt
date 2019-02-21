package com.pixelplex.pixelmvisample.activity.githubusers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixelplex.pixelmvisample.R
import com.pixelplex.pixelmvisample.model.RemoteGithubUser
import com.pixelplex.pixelmvisample.tools.PaginationManager
import kotlinx.android.synthetic.main.user_list_item.view.*

class GithubUsersRecyclerViewAdapter(
    private val click: (Int) -> Unit,
    private val loadMore: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<RemoteGithubUser> = arrayListOf()

    private lateinit var paginationManager: PaginationManager

    fun getItem(position: Int): RemoteGithubUser {
        return items[position]
    }

    fun notifyBatch(newItems: List<RemoteGithubUser>) {
        paginationManager.isLastPage = false
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start + 1, itemCount)
        paginationManager.isLoading = false
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        paginationManager = PaginationManager.assign(recyclerView, loadMore)
    }

    override fun getItemViewType(position: Int) = if (position < items.size) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == 0) {
            GithubUserViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false),
                click
            )
        } else {
            LoaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loader_list_item,
                    parent,
                    false
                )
            )
        }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < items.size) {
            (holder as GithubUserViewHolder).bind(items[position])
        }
    }
}

class GithubUserViewHolder(itemView: View, private val click: (Int) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            click(adapterPosition)
        }
    }

    fun bind(item: RemoteGithubUser) {
        itemView.tv_login.text = item.login
        Glide.with(itemView.context)
            .load(item.avatarUrl)
            .into(itemView.iv_icon)
    }
}

class LoaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)