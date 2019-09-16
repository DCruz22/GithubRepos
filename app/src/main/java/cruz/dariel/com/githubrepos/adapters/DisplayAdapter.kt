package cruz.dariel.com.githubrepos.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cruz.dariel.com.githubrepos.R
import cruz.dariel.com.githubrepos.extensions.toast
import cruz.dariel.com.githubrepos.models.Repository
import kotlinx.android.synthetic.main.list_item.view.*


class DisplayAdapter(private val listener: Listener, private val context: Context, private var repositoryList: List<Repository>) : RecyclerView.Adapter<DisplayAdapter.MyViewHolder>() {

    interface Listener{
        fun onItemClicked(current: Repository?)
        fun onBookmarkImgClicked(current: Repository?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = repositoryList[position]
        holder.setData(current, position)
    }

    override fun getItemCount(): Int = repositoryList.size

    fun swap(dataList: List<Repository>) {
        if (dataList.isEmpty())
            context.toast("No Items Found")
        this.repositoryList = dataList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View, listener: Listener) : RecyclerView.ViewHolder(itemView) {

        private var pos: Int = 0
        private var current: Repository? = null

        init {

            itemView.imgBookmark.setOnClickListener { listener.onBookmarkImgClicked(current) }

            itemView.setOnClickListener {listener.onItemClicked(current)}
        }

        fun setData(current: Repository?, position: Int) {

            current?.let {
                itemView.txvName.text = current.name
                itemView.txvLanguage.text = current.language
                itemView.txvForks.text = current.forks.toString()
                itemView.txvWatchers.text = current.watchers.toString()
                itemView.txvStars.text = current.stars.toString()
            }
            this.pos = position
            this.current = current
        }
    }


    companion object {

        private val TAG = DisplayAdapter::class.java.simpleName
    }
}
