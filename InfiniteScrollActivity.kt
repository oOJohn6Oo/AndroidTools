package com.github.john.test

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


@SuppressLint("Registered")
class InfiniteScrollActivity : AppCompatActivity() {

    private val handler = Handler()
    lateinit var changeItemRunnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resList =
            mutableListOf<Int>(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground)
        val manager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val adapter = InfiniteScrollAdapter(this, resList)
        val snapHelper = PagerSnapHelper()

        // 配置列表
        recycler_view_main.apply {
            layoutManager = manager
            this.adapter = adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        changeBanner()
                        snapHelper.findSnapView(manager)?.apply {
                            // 指示器
                            (this@InfiniteScrollActivity.radio_group_main[recycler_view_main.getChildAdapterPosition(
                                this
                            ) % resList.size] as RadioButton).isChecked = true
                        }
                    } else stopBanner()
                }
            })
        }


        changeItemRunnable = Runnable {
            val pos = recycler_view_main.getChildAdapterPosition(snapHelper.findSnapView(manager)!!)
            recycler_view_main.smoothScrollToPosition(pos + 1)
        }

        handler.post {
            // 无限滚动可以左滑
            recycler_view_main.scrollToPosition(Int.MAX_VALUE.shr(1))
            // 初始化指示器
            recycler_view_main.smoothScrollToPosition(Int.MAX_VALUE.shr(1) + 1)
        }
    }

    fun changeBanner() {
        stopBanner()
        handler.postDelayed(changeItemRunnable, 5000)
    }

    fun stopBanner(): Unit = handler.removeCallbacks(changeItemRunnable)

    override fun onResume() {
        super.onResume()
        // 滚动
        changeBanner()
    }
    override fun onPause() {
        super.onPause()
        // 防止资源消耗
        stopBanner()
    }


    class InfiniteScrollAdapter(
        val context: Context,
        private val drawableResList: MutableList<Int>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val nowData = drawableResList[position % drawableResList.size]
            (holder.itemView as ImageView).setImageResource(nowData)
        }

        override fun getItemCount(): Int = Int.MAX_VALUE

        override fun getItemId(position: Int): Long =
            super.getItemId(position % drawableResList.size)
    }

}