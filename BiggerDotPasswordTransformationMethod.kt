class BiggerDotPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(super.getTransformation(source, view))
    }

    private class PasswordCharSequence(private val sequence: CharSequence) : CharSequence by sequence {
        val dot = '\u2022'
        val bigDot = '●'
        override fun get(index: Int): Char = if (sequence[index]==dot) bigDot else sequence[index]
    }

}