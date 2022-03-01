package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.databinding.WidgetListUserBinding

class UserListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListUserBinding =
        WidgetListUserBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: UserEntity) {
        binding.txtFullName.text = model.getFullName()
    }

}