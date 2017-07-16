package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(
      temperatures: Iterable[(Location, Double)]
    ): (Int, Int) => Double = {

    import Visualization._
    val grid = (for {
      φ <-  -89 to  +90
      λ <- -180 to +179
      location = Location(φ.toDouble, λ.toDouble)
      t = predictTemperature(temperatures, location)
    } yield  {
      (φ, λ) -> t
    }).toMap

    (φ: Int, λ: Int) => grid((φ, λ))
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(
      temperaturess: Iterable[Iterable[(Location, Double)]]
    ): (Int, Int) => Double = {

    val gridsCount = for {
      ts <- temperaturess
      grid = makeGrid(ts)
    } yield {
      (grid, 1)
    }

    val reducedGrids =
      gridsCount.reduce { (gn1, gn2) =>
        val (g1, n1) = gn1
        val (g2, n2) = gn2

        val g = (φ: Int, λ: Int) => g1(φ, λ) + g2(φ, λ)
        val n = n1 + n2
        (g, n)
      }

    val (g, n) = reducedGrids
    (φ: Int, λ: Int) => g(φ, λ) / n
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(
      temperatures: Iterable[(Location, Double)],
      normals: (Int, Int) => Double
    ): (Int, Int) => Double = {

    val grid = makeGrid(temperatures)
    (φ: Int, λ: Int) => grid(φ, λ)- normals(φ, λ)
  }
}

