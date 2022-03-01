package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.databinding.WidgetBannerBinding

class BannerWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetBannerBinding =
        WidgetBannerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(title: String) {
        binding.txtTitle.text = title
    }

}