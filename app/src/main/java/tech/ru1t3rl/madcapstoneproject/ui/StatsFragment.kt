package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val run = arguments?.getSerializable("ARG_ACTIVE_RUN") as Run?

        if (run != null) {
            val localScore = run.distance.toFloat() *
                    (run.distance.toFloat() / (run.time / 1000 / 60 / 60)) *
                    (run.time / 1000 / 60 / 60)

            binding.tvDistance.text = run.distance
            binding.tvSpeed.text = run.distance
            binding.tvScore.text = localScore.toString()
        }
    }
}