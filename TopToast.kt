class TopToast(val context: Context) :Toast(context){

    private var topText: String = ""
    private val museView: TextView = TextView(context).apply {
        setBackgroundColor(Color.parseColor("#BB725A7C"))
        gravity = Gravity.CENTER
        setTextColor(ContextCompat.getColor(context, android.R.color.white))
        setTextIsSelectable(false)
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MuseUtil.newDp2px(40f))
    }

    init {
        view = FrameLayout(context).apply {
            addView(museView)
	    // 不设置会默认留出状态栏高度的距离
            systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
	// 设置顶部titleBar高度的偏移
        setGravity(Gravity.TOP+Gravity.FILL_HORIZONTAL, 0, MuseUtil.newDp2px(64f))
    }

    fun setTopText(text:String):TopToast{
        topText = text
        return this
    }
    fun setTopText(@StringRes stringRes:Int):TopToast{
        topText = context.getString(stringRes)
        return this
    }
       
    override fun getDuration(): Int = LENGTH_SHORT

    override fun show() {
        museView.text = topText
        super.show()
    }

}