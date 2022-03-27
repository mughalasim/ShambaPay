package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ke.co.shambapay.databinding.WidgetLoadingBinding

class LoadingWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetLoadingBinding =
        WidgetLoadingBinding.inflate(LayoutInflater.from(context), this, true)

    fun update(message: String, showLoading: Boolean) {
        binding.progressBar.isVisible = showLoading
        binding.txtMessage.text = message
        binding.txtMessage.isVisible = message.isNotEmpty()
    }

}