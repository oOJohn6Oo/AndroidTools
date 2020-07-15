class TabItem(context: Context, attrs: AttributeSet?, defStyleInt: Int) : View(context, attrs, defStyleInt) {
    private var tabIcon: Drawable? = null
    private var tabTitle: String = ""
    private var tabTitleSize: Float = 24f
    private var tabTitleColor: ColorStateList? = ContextCompat.getColorStateList(context, R.color.selector_tab_bar_color)
    private var iconPadding: Int = 8
    private var textPaint: Paint = Paint()
    var showBadge: Boolean = false
    private var badgeSize: Int = if (isInEditMode) 8 else 6.dp.toInt()
    private var defaultTextColor = ContextCompat.getColor(context, R.color.font_9_color)

    private var intrinsicWidth: Int = 0
    private var intrinsicHeight: Int = 0

    private var titleL: Float = 0f
    private var titleT: Float = 0f

    private var iconL: Int = 0
    private var iconR: Int = 0
    private var iconT: Int = 0
    private var iconB: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    init {
        isClickable = true
        isFocusable = true
        val bgdTa = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, bgdTa, true)
        setBackgroundResource(bgdTa.resourceId)

        val dp4 = 8.takeIf { isInEditMode } ?: 4.dp.toInt()
        val sp12 = 24.takeIf { isInEditMode } ?: 12.sp.toInt()
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.TabItem)
            tabTitle = ta.getString(R.styleable.TabItem_tab_title) ?: "Tab"
            tabTitleSize = ta.getDimensionPixelSize(R.styleable.TabItem_tab_title_size, sp12).toFloat()
            tabTitleColor = ta.getColorStateList(R.styleable.TabItem_tab_title_color)
            iconPadding = ta.getDimensionPixelSize(R.styleable.TabItem_tab_icon_padding, dp4)
            tabIcon = ta.getDrawable(R.styleable.TabItem_tab_icon)
            badgeSize = ta.getDimensionPixelSize(R.styleable.TabItem_tab_badge_size, dp4 * 2)
            setTabIcon(tabIcon, ta.getDimensionPixelSize(R.styleable.TabItem_tab_icon_size, -1))
            ta.recycle()
        }
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        textPaint.textSize = tabTitleSize
        val titleWidth = textPaint.measureText(tabTitle)
        val titleHeight = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top
        canvas?.let {
            titleL = (measuredWidth - titleWidth) / 2f
            titleT = (measuredHeight - titleHeight + (0.takeIf { intrinsicHeight == 0 } ?: (intrinsicHeight + iconPadding))) / 2f
            tabIcon?.let { icon ->
                iconL = (measuredWidth - intrinsicWidth) / 2
                iconR = iconL + intrinsicWidth
                iconT = (measuredHeight - titleHeight.toInt() - (0.takeIf { intrinsicHeight == 0 } ?: (iconPadding + intrinsicHeight))) / 2
                iconB = iconT + intrinsicHeight
                drawDrawable(icon, it)
                if (showBadge) drawBadge(it)
            }
            drawTitle(it, titleHeight.toInt())
        }
    }

    private fun drawBadge(canvas: Canvas) {
        textPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawCircle(iconR - badgeSize / 2f, iconT + badgeSize / 2f, badgeSize / 2f, textPaint)
    }

    private fun drawDrawable(icon: Drawable, it: Canvas) {
        icon.setBounds(iconL, iconT, iconR, iconB)
        icon.draw(it)
    }

    private fun drawTitle(it: Canvas, height: Int) {
        textPaint.color = tabTitleColor?.getColorForState(drawableState, defaultTextColor) ?: defaultTextColor
        it.drawText(tabTitle, titleL, titleT + height - textPaint.fontMetrics.bottom, textPaint)
        it.save()
        it.restore()
    }


    fun setTabIcon(drawable: Drawable?, sizeInPx: Int): TabItem {
        tabIcon = drawable
        return setTabIconSize(sizeInPx)
    }

    fun setTabIconSize(sizeInPx: Int): TabItem {
        if (sizeInPx == -1) {
            tabIcon?.let {
                intrinsicWidth = it.intrinsicWidth
                intrinsicHeight = it.intrinsicHeight
            }
        } else {
            intrinsicWidth = sizeInPx
            intrinsicHeight = sizeInPx
        }
        return this
    }

    fun setBadgeSize(sizeInPx: Int): TabItem {
        if (sizeInPx != -1) badgeSize = sizeInPx
        return this
    }

    fun setTitle(@StringRes resString: Int): TabItem {
        tabTitle = context.getString(resString)
        return this
    }

    fun setTitle(title: String): TabItem {
        tabTitle = title
        return this
    }

    /**
     * 设置title字体大小
     */
    fun setTitleSize(sizeInPx: Float): TabItem {
        if (sizeInPx != -1f) tabTitleSize = sizeInPx
        return this
    }

    /**
     * 设置title与Icon间距
     */
    fun setIconPadding(paddingInDp: Int): TabItem {
        if (paddingInDp != -1) iconPadding = paddingInDp.dp.toInt()
        return this
    }


    /**
     * 设置title字体颜色
     */
    fun setTitleColor(color: ColorStateList?): TabItem {
        if (color != null) tabTitleColor = color
        return this
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        tabIcon?.let { icon ->
            icon.setState(drawableState).takeIf { icon.isStateful }
        }
    }

}