class TabBar(context: Context, attrs: AttributeSet?, defStyleInt: Int) : ViewGroup(context, attrs, defStyleInt) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private val layoutLeft: MutableList<Int> = mutableListOf()
    private val layoutTop: MutableList<Int> = mutableListOf()
    private val layoutRight: MutableList<Int> = mutableListOf()
    private val layoutBottom: MutableList<Int> = mutableListOf()
    private var barSizePortrait: Int = 0
    private var barSizeLandScape: Int = 0
    private var currentTab: Int = -1

    init {
        setBackgroundColor(Color.WHITE)
        elevation = 12f.takeIf { isInEditMode } ?: 12.dp
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabBar)
        barSizePortrait = ta.getDimensionPixelSize(R.styleable.TabBar_tab_bar_height_p, 0)
        barSizeLandScape = ta.getDimensionPixelSize(R.styleable.TabBar_android_layout_width, 0)


        val tabCount = ta.getInt(R.styleable.TabBar_tab_count, 0)
        if (tabCount > 0) {
            val titles = ta.getTextArray(R.styleable.TabBar_tab_bar_titles)
            val resIcon = ta.getResourceId(R.styleable.TabBar_tab_bar_icons, -1)
            val iconTa = resources.obtainTypedArray(resIcon)

            val icons = IntArray(iconTa.length())
            for (i in 0 until iconTa.length()) {
                icons[i] = iconTa.getResourceId(i, 0)
            }
            iconTa.recycle()
            val textColor = ta.getColorStateList(R.styleable.TabBar_tab_title_color)
            val textSize = ta.getDimension(R.styleable.TabBar_tab_title_size, -1f)
            val iconPadding = ta.getDimensionPixelSize(R.styleable.TabBar_tab_icon_padding, -1)
            val badgeSize = ta.getDimensionPixelSize(R.styleable.TabBar_tab_badge_size, -1)
            val tabIconSize = ta.getDimensionPixelSize(R.styleable.TabBar_tab_icon_size, -1)

            for (i in 0 until tabCount) {
                addView(
                    TabItem(context).setTitle(titles[i].toString()).setTabIcon(ContextCompat.getDrawable(context, icons[i]), tabIconSize)
                        .setTitleColor(textColor).setTitleSize(textSize).setIconPadding(iconPadding).setBadgeSize(badgeSize)
                )
            }
        }
        ta.recycle()
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 参数初始化
        layoutLeft.clear()
        layoutTop.clear()
        layoutRight.clear()
        layoutBottom.clear()
        val isLandScape = if (isInEditMode) false else isLandScape(context)
        val childWidth: Int
        val childHeight: Int

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))

        if (isLandScape) {
            childWidth = measuredWidth
            childHeight = measuredHeight / childCount
        } else {
            childWidth = measuredWidth / childCount
            childHeight = measuredHeight
        }
//        setMeasuredDimension(desiredWidth, desiredHeight)
        // 测量 子View
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
            val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            if (isLandScape) {
                layoutLeft.add(0)
                layoutTop.add(i * childHeight)
                layoutRight.add(childWidth)
                layoutBottom.add((i + 1) * childHeight)
            } else {
                // 竖屏
                layoutLeft.add(i * childWidth)
                layoutTop.add(0)
                layoutRight.add((i + 1) * childWidth)
                layoutBottom.add(childHeight)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) getChildAt(i).layout(layoutLeft[i], layoutTop[i], layoutRight[i], layoutBottom[i])
    }

     fun fitOrientation() {
        if (isLandScape(context)) {
            layoutParams.width = barSizeLandScape
            layoutParams.height = MATCH_PARENT
        } else {
            layoutParams.width = MATCH_PARENT
            layoutParams.height = barSizePortrait
        }
    }

    fun setCurrentTab(currentTab: Int) {
        // 避免重复点击
        if (currentTab == this.currentTab) {
            return
        }
        // 处理第一次选中事件
        if (this.currentTab != -1) getChildAt(this.currentTab).isSelected = false
        this.currentTab = currentTab
        getChildAt(this.currentTab).isSelected = true

    }

    fun getCurrentTab() = currentTab

    fun setTabClickListener(listener: OnItemClickListener) {
        for (i in 0 until childCount) getChildAt(i).setOnClickListener {
            setCurrentTab(i)
            listener.onItemSelected(i)
        }
    }

    fun setTabLongClickListener(listener: OnItemLongClickListener) {
        for (i in 0 until childCount) getChildAt(i).setOnLongClickListener {
            listener.onLongClick(i)
        }
    }

    fun isBadgeShowing(pos: Int) = (getChildAt(pos) as TabItem).showBadge

    fun showBadge(pos: Int) {
        (getChildAt(pos) as TabItem).showBadge = true
        getChildAt(pos).invalidate()
    }

    fun hideBadge(pos: Int) {
        (getChildAt(pos) as TabItem).showBadge = false
        getChildAt(pos).invalidate()
    }
}