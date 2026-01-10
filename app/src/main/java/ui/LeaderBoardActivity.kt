package ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cargame.databinding.ActivityLeaderBoardBinding

class LeaderBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderBoardBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            setupFragments()
        }


    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction()
            .add(binding.containerList.id, TopTenFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .add(binding.containerMap.id, MapFragment())
            .commit()
    }

    fun updateMapLocation(lat: Double, lon: Double) {

        val mapFrag = supportFragmentManager.findFragmentById(binding.containerMap.id) as? MapFragment

        if (mapFrag == null) {
            Toast.makeText(this, "ERROR: Map Fragment NOT found!", Toast.LENGTH_LONG).show()
        } else {
            mapFrag.zoomToLocation(lat, lon)
        }
    }
}