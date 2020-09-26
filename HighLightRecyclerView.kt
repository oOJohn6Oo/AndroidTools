class HighLightRecyclerView(context: Context, attr: AttributeSet?) : RecyclerView(context, attr) {
    constructor(context: Context) : this(context, null)

    lateinit var animator: ValueAnimator
    private var hasHighLightHeightInitialized = false
    var highLightHeight: Int = 0
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }
    var paint = Paint()
    var path = Path()

    init {
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#99000000")
        // addDecoration
        val decoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                this@HighLightRecyclerView.adapter?.let {
                    if (parent.getChildAdapterPosition(view) == 0)
                        outRect.top = (parent.measuredHeight - view.layoutParams.height).shr(1)
                    else if (parent.getChildAdapterPosition(view) == it.itemCount - 1)
                        outRect.bottom = (parent.measuredHeight - view.layoutParams.height).shr(1)
                }
            }
        }
        addItemDecoration(decoration)
        // attach SnapHelper
        val snapHelper = object : LinearSnapHelper() {
            override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
                return when {
                    layoutManager == null -> null
                    layoutManager.childCount == 0 -> null
                    else -> {
                        var closestChild: View? = null
                        var absClosest = Int.MAX_VALUE
                        val helper = OrientationHelper.createVerticalHelper(layoutManager)
                        val center = helper.startAfterPadding + helper.totalSpace.shr(1)

                        var childCenter: Int
                        var distance: Int
                        for (i in 0 until layoutManager.childCount) {
                            layoutManager.getChildAt(i)?.let {
                                childCenter = if (i == 0)
                                    helper.getDecoratedStart(it) + helper.getDecoratedMeasurement(it) - it.layoutParams.height
                                else helper.getDecoratedStart(it) + it.layoutParams.height.shr(1)
                                distance = abs(center - childCenter)
                                if (distance < absClosest) {
                                    absClosest = distance
                                    closestChild = it
                                }
                            }
                        }
                        closestChild
                    }
                }
            }
        }
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    snapHelper.findSnapView(layoutManager)?.let {
                        animator = ValueAnimator.ofInt(highLightHeight, it.layoutParams.height)
                            .setDuration(300)
                        animator.removeAllUpdateListeners()
                        animator.addUpdateListener { va ->
                            highLightHeight = va.animatedValue as Int
                        }
                        animator.start()

                    }
                }
            }
        })
        hasFixedSize()
        snapHelper.attachToRecyclerView(this)
    }

    fun initHighLightHeight(initialHeight: Int) {
        if (!hasHighLightHeightInitialized) {
            highLightHeight = initialHeight
            hasHighLightHeightInitialized = true
        }
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        path.reset()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        path.addRect(
            0f,
            (height - highLightHeight).shr(1).toFloat(),
            width.toFloat(),
            (height + highLightHeight).shr(1).toFloat(),
            Path.Direction.CCW
        )
        canvas?.drawPath(path, paint)
    }
}