package data.cargame

data class Record(
    val name: String,
    val score: Double,
    val lat: Double,
    val lon: Double,
    val mode: String,
    val isFast: Boolean
)
