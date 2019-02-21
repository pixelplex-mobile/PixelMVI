package com.pixelplex.pixelmvisample.tools

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationManager private constructor(
    private val loadMore: () -> Unit
) {

    companion object {
        fun assign(
            recyclerView: RecyclerView,
            loadMore: () -> Unit
        ): PaginationManager {
            val instance = PaginationManager(loadMore)
            recyclerView.addOnScrollListener(instance.recyclerViewOnScrollListener)
            return instance
        }
    }

    var isLoading = false

    var isLastPage = false

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager?.childCount ?: 0
            val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                    ?: 0

            if (!isLoading && !isLastPage) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    loadMore()
                    isLoading = true
                }
            }
        }
    }
}