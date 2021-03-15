package namba.wallet.nambaone.uikit.widget

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import namba.wallet.nambaone.common.utils.extensions.onDrawableClicked
import namba.wallet.nambaone.uikit.R
import kotlin.math.roundToInt

class NambaTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), TextWatcher {

    private lateinit var labelTextView: TextView
    private lateinit var textField: EditText
    private lateinit var hintTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var constraint: ConstraintLayout
    private lateinit var dropdownIcon: ImageView

    private var labelColor: Int = 0
    private var textColor: Int = 0
    private var focusedBorderColor: Int = 0
    private var borderColor: Int = 0
    private var errorColor: Int = 0

    private var inputType: Int = 0
    private var imeOptions: Int = 0

    private var cornerRadius: Float = 0f
    private var borderStrokeWidthFocused: Float = 0f
    private var borderStrokeWidth: Float = 0f

    private var isEditable: Boolean = true
    private var isDropdown: Boolean = false

    private var labelText: String = ""
    private var hintText: String = ""
    private var placeholderText: String = ""

    private var maxLength: Int = -1

    private lateinit var bg: GradientDrawable
    private lateinit var bgDisabled: GradientDrawable
    private lateinit var bgFocused: GradientDrawable
    private lateinit var bgError: GradientDrawable

    var afterTextChanged: ((String) -> Unit)? = null

    init {
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.NambaTextField, 0, 0)
        initAttrs(styleAttrs)
        initDimensions()
        initDrawables(styleAttrs)
        styleAttrs.recycle()
        inflateViews()
        setupTextField()
        setupHintTextView()
        setupLabelTextView()

        labelTextView.setTextColor(labelColor)
        errorTextView.setTextColor(errorColor)
        hintTextView.setTextColor(labelColor)

        constraint.layoutTransition = LayoutTransition()
        constraint.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING and LayoutTransition.CHANGE_DISAPPEARING)
        constraint.layoutTransition.setDuration(200)
    }

    private fun setupLabelTextView() {
        labelTextView.text = labelText
        labelTextView.isVisible = labelText.isNotEmpty()
    }

    private fun setupHintTextView() {
        hintTextView.text = hintText
        hintTextView.isVisible = hintText.isNotEmpty()
    }

    private fun inflateViews() {
        View.inflate(context, R.layout.widget_namba_edit_text, this)
        labelTextView = findViewById(R.id.labelTextView)
        textField = findViewById(R.id.nambaEditText)
        hintTextView = findViewById(R.id.holderTextView)
        errorTextView = findViewById(R.id.errorTextView)
        dropdownIcon = findViewById(R.id.dropdownIcon)
        constraint = findViewById(R.id.container)
    }

    private fun initAttrs(attrs: TypedArray) {
        textColor = attrs.getColor(
            R.styleable.NambaTextField_android_textColor,
            Color.parseColor("#222324")
        )
        focusedBorderColor = attrs.getColor(R.styleable.NambaTextField_focusedBorderColor, Color.GRAY)
        borderColor = attrs.getColor(R.styleable.NambaTextField_borderColor, Color.parseColor("#909399"))
        isEditable = attrs.getBoolean(R.styleable.NambaTextField_editable, true)
        isDropdown = attrs.getBoolean(R.styleable.NambaTextField_isDropdown, false)
        inputType =
            attrs.getInt(
                R.styleable.NambaTextField_android_inputType,
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            )
        imeOptions = attrs.getInt(R.styleable.NambaTextField_android_imeOptions, EditorInfo.IME_ACTION_DONE)
        errorColor = Color.parseColor("#FF2D55")
        labelColor = Color.parseColor("#909399")
        maxLength = attrs.getInt(R.styleable.NambaTextField_android_maxLength, -1)
        labelText = attrs.getString(R.styleable.NambaTextField_labelText) ?: ""
        hintText = attrs.getString(R.styleable.NambaTextField_hintText) ?: ""
        placeholderText = attrs.getString(R.styleable.NambaTextField_android_hint) ?: ""
    }

    private fun setupTextField() {
        textField.setOnClickListener {
            textField.setSelection(textField.text.length)
        }
        textField.background = bg
        textField.addTextChangedListener(this)

        textField.inputType = inputType
        textField.imeOptions = imeOptions
        textField.setTextColor(textColor)
        textField.hint = placeholderText
        if (maxLength > 0) {
            textField.filters += InputFilter.LengthFilter(maxLength)
        }
        textField.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            textField.background = if (hasFocus) bgFocused else bg
        }

        if (!isEditable) {
            textField.isFocusable = false
            textField.isClickable = true
            textField.background = null
            textField.isCursorVisible = false
            textField.background = bgDisabled
        }

        if (isDropdown) {
            textField.isFocusable = false
            textField.isClickable = true
            textField.isCursorVisible = false
            dropdownIcon.isVisible = true
        } else {
            dropdownIcon.isVisible = false
        }
    }

    private fun initDimensions() {
        cornerRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
        borderStrokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, .5f, resources.displayMetrics)
        borderStrokeWidthFocused =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }

    private fun initDrawables(styleAttrs: TypedArray) {
        val focusedBorderColor = styleAttrs.getColor(
            R.styleable.NambaTextField_focusedBorderColor,
            Color.parseColor("#F48037")
        )
        val borderColor =
            styleAttrs.getColor(R.styleable.NambaTextField_borderColor, Color.parseColor("#909399"))

        bgFocused = GradientDrawable()
        bgFocused.shape = GradientDrawable.RECTANGLE
        bgFocused.cornerRadius = cornerRadius
        bgFocused.setStroke(borderStrokeWidthFocused.roundToInt(), focusedBorderColor)

        bg = GradientDrawable()
        bg.shape = GradientDrawable.RECTANGLE
        bg.cornerRadius = cornerRadius
        bg.setStroke(borderStrokeWidth.roundToInt(), borderColor)

        bgError = GradientDrawable()
        bgError.shape = GradientDrawable.RECTANGLE
        bgError.cornerRadius = cornerRadius
        bgError.setStroke(borderStrokeWidthFocused.roundToInt(), errorColor)

        bgDisabled = GradientDrawable()
        bgDisabled.shape = GradientDrawable.RECTANGLE
        bgDisabled.cornerRadius = cornerRadius
        bgDisabled.setStroke(borderStrokeWidth.roundToInt(), Color.parseColor("#E2E8F2"))
        bgDisabled.setColor(Color.parseColor("#F5F8FD"))
    }

    fun setLabel(label: String) {
        labelText = label
        labelTextView.text = labelText
        labelTextView.isVisible = labelText.isNotEmpty()
    }

    fun setHint(hint: String) {
        hintText = hint
        hintTextView.text = hintText
        hintTextView.isVisible = hintText.isNotEmpty()
    }

    fun setText(text: CharSequence?) {
        textField.setText(text)
    }

    fun setDrawableEnd(@DrawableRes iconRes: Int) {
        textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0)
    }

    fun onDrawableClicked(block: () -> Unit) {
        textField.onDrawableClicked(onRightClicked = { block.invoke() })
    }

    fun getText(): Editable = textField.text

    fun getEditText() = textField

    fun setInputType(inputType: Int) {
        textField.inputType = inputType
    }

    fun setError(message: String) {
        errorTextView.text = message
        textField.background = bgError
        errorTextView.isVisible = true
        hintTextView.isVisible = false
    }

    fun onDetach() {
        textField.background = bg
    }

    fun removeError() {
        labelTextView.text = labelText
        textField.background = if (textField.isFocused) bgFocused else bg
        errorTextView.isVisible = false
        hintTextView.isVisible = true
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        afterTextChanged?.invoke(p0.toString())
        removeError()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        constraint.isEnabled = enabled
        textField.background = if (enabled) bg else bgDisabled
        textColor = Color.parseColor(if (enabled) "#222324" else "#E2E8F2")
        textField.setTextColor(textColor)
        textField.isEnabled = enabled
    }
}