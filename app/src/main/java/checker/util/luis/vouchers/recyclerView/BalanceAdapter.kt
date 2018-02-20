package checker.util.luis.vouchers.recyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import checker.util.luis.vouchers.R
import checker.util.luis.vouchers.database.entity.BalanceEntity
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class BalanceAdapter constructor(context: Context) : RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>() {

    inner class BalanceViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val balanceName: TextView = itemView.recycler_textView_name
        val balanceAmount : TextView = itemView.recycler_textView_amount
    }

    // Adapter vars, we use this joined assignment as constructor
    private val mInflater : LayoutInflater = LayoutInflater.from(context)
    private var mBalance : List<BalanceEntity>? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BalanceViewHolder {
        val itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return BalanceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        if (mBalance != null) {
            val current : BalanceEntity  = mBalance!![position]
            holder.balanceAmount.text = current.amount
            holder.balanceName.text = current.name
        } else {
            holder.balanceAmount.text = "$0.0"
            holder.balanceName.text = "unknown"
        }
    }

    override fun getItemCount(): Int {
        return if (mBalance != null) {
            mBalance!!.size
        } else { 0 }
    }

    fun updateAdapter(balance: List<BalanceEntity>?) {
        balance?.let {
            mBalance = it
            notifyDataSetChanged()
        }
    }

}