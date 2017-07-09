package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class Station(
    id: String,
    latitude: Double,
    longitude: Double
  )

case class Observation(
    id: String,
    month: Int,
    day: Int,
    temperature: Double
  )

case class LocalizedObservation(
    id: String,
    month: Int,
    day: Int,
    temperature: Double,
    latitude: Double,
    longitude: Double
  )

