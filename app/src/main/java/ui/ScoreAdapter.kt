package ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import interfaces.CallbackHighScoreItemClicked
import com.example.cargame.R
import data.cargame.Record
import com.example.cargame.databinding.ItemScoreBinding

class ScoreAdapter(
    private val scores: ArrayList<Record>,
    private val callback: CallbackHighScoreItemClicked
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = scores[position]

        holder.binding.lblName.text = record.name
        holder.binding.lblScore.text = "%.1f m".format(record.score)
        holder.binding.lblRank.text = "${position + 1}"
        if(record.mode == "ARROWS") {
            holder.binding.imgModeIcon.setImageResource(R.drawable.navigation_orange)
            } else {
            holder.binding.imgModeIcon.setImageResource(R.drawable.console)
        }
        if (record.isFast) {
            holder.binding.imgSpeedMode.setImageResource(R.drawable.fast)
        } else {
            holder.binding.imgSpeedMode.setImageResource(R.drawable.slow)
        }

        holder.binding.root.setOnClickListener {
            callback.highScoreItemClicked(record.lat, record.lon)
        }
    }

    override fun getItemCount() = scores.size

    class ScoreViewHolder(val binding: ItemScoreBinding) : RecyclerView.ViewHolder(binding.root)
}