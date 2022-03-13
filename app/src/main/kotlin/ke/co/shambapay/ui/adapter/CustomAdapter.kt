package ke.co.shambapay.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ke.co.shambapay.R
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.ui.widget.EmployeeListWidget
import ke.co.shambapay.ui.widget.UserListWidget
import ke.co.shambapay.ui.widget.WorkListWidget

class CustomAdapter<T>(private val dataSet: MutableList<T>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private lateinit var internalListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(data: Any?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        internalListener = listener
    }

    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        val widgetEmployeeItem: EmployeeListWidget = view.findViewById(R.id.widget_list_employee)
        val widgetWorkItem: WorkListWidget = view.findViewById(R.id.widget_list_work)
        val widgetUserItem: UserListWidget = view.findViewById(R.id.widget_list_user)
    }

    override fun onCreateViewHolder(vg: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(vg.context).inflate(R.layout.list_item_recycler, vg, false)
        return ViewHolder(view, internalListener)
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        vh.widgetEmployeeItem.visibility = View.VISIBLE
        vh.widgetWorkItem.visibility = View.GONE
        vh.widgetUserItem.visibility = View.GONE

        when(dataSet[position]){
            is EmployeeEntity -> {
                vh.widgetEmployeeItem.visibility = View.VISIBLE
                vh.widgetEmployeeItem.setUp(dataSet[position] as EmployeeEntity)
                vh.widgetEmployeeItem.setOnClickListener { internalListener.onItemClicked(dataSet[position]) }
            }

            is WorkEntity -> {
                vh.widgetWorkItem.visibility = View.VISIBLE
                vh.widgetWorkItem.setUp(dataSet[position] as WorkEntity)
                vh.widgetWorkItem.setOnClickListener { internalListener.onItemClicked(dataSet[position]) }
            }

            is UserEntity -> {
                vh.widgetUserItem.visibility = View.VISIBLE
                vh.widgetUserItem.setUp(dataSet[position] as UserEntity)
                vh.widgetUserItem.setOnClickListener { internalListener.onItemClicked(dataSet[position]) }
            }
        }
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