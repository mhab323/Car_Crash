package ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import interfaces.CallbackHighScoreItemClicked
import utilities.HighScoreManager
import data.cargame.Record
import com.example.cargame.databinding.FragmentTopTenBinding

class TopTenFragment : Fragment() {

    private var _binding: FragmentTopTenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopTenBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setUpHomeBtn()

        return binding.root
    }

    private fun setUpHomeBtn() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }    }

    private fun setupRecyclerView() {
        val manager = HighScoreManager(requireContext())
        val scoresList = manager.getScores()

        while (scoresList.size < 10) {
            scoresList.add(Record(name = "-", score = 0.0, lat = 0.0, lon = 0.0, mode = "", isFast = false))
        }


        val adapter = ScoreAdapter(scoresList, object : CallbackHighScoreItemClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {

                if (lat == 0.0 && lon == 0.0) {
                    Toast.makeText(requireContext(), "No location data!", Toast.LENGTH_SHORT).show()
                    return
                } 

                val activity = requireActivity() as? LeaderBoardActivity
                activity?.updateMapLocation(lat, lon)

            }
        })

        binding.topTenLSTScores.layoutManager = LinearLayoutManager(requireContext())
        binding.topTenLSTScores.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}