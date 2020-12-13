package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentStatsBinding
import tech.ru1t3rl.madcapstoneproject.model.Run

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStats()
    }

    private fun loadStats() {
        val run = arguments?.getSerializable("ARG_ACTIVE_RUN") as Run?

        if (run != null) {
            val distance = run.distance.replace(',', '.').toFloat()

            val localScore =
                (distance * (run.time/1000f/60/60) * (distance / (run.time / 1000f/ 60 / 60)) * 10000).toInt()

            binding.tvDistanceValue.text = getString(R.string.battle_distance, distance)
            binding.tvSpeedValue.text = getString(R.string.battle_speed, distance / (run.time / 1000f/ 60 / 60))
            binding.tvScoreValue.text = localScore.toString()
        }
    }

    override fun onStart() {
        super.onStart()

        loadStats()
    }

    override fun onResume() {
        super.onResume()

        loadStats()
    }
}