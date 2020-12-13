package tech.ru1t3rl.madcapstoneproject.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.ItemHistoryRunBinding
import tech.ru1t3rl.madcapstoneproject.model.Run

class HistoryAdapter(
    private val runs: List<Run>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHistoryRunBinding.bind(itemView)

        fun databind(run: Run) {
            binding.tvDateValue.text = run.date

            binding.tvDistanceValue.text = java.lang.String.format(
                binding.tvDistanceValue.text.toString(),
                run.distance.replace(',', '.').toFloat()
            )

            binding.tvSpeedValue.text = java.lang.String.format(
                binding.tvSpeedValue.text.toString(),
                run.averageSpeed.replace(',', '.').toFloat()
            )

            binding.tvScoreValue.text = java.lang.String.format(
                binding.tvScoreValue.text.toString(),
                run.score
            )
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_history_run,
                parent,
                false
            )
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return runs.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(runs[position])
    }
}