package ke.co.shambapay.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ke.co.shambapay.databinding.ListItemRecyclerBinding

class CustomAdapter<T>(private val dataSet: MutableList<T>) : RecyclerView.Adapter<CustomViewHolder>() {

    lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(data: Any?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    override fun onCreateViewHolder(vg: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(ListItemRecyclerBinding.inflate(LayoutInflater.from(vg.context), vg, false), clickListener)
    }

    override fun onBindViewHolder(vh: CustomViewHolder, position: Int) {
        vh.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(dataSet: List<T>){
        this.dataSet.clear()
        dataSet.map { data ->
            this.dataSet.add(data)
        }
        notifyDataSetChanged()
    }

}