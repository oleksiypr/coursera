package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = Signal{     
    val aVal = a()
    val bVal = b()
    val cVal = c()
    bVal*bVal - 4.0*aVal*cVal
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
                       c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = Signal{
    val aVal = a()
    val bVal = b()
    val deltaVal = delta()
    c()

    if (deltaVal < 0.0) Set.empty else 
    if (deltaVal == 0.0) Set(-bVal / (2.0 * aVal)) else {
      import math.sqrt
      val x1 = (-bVal - sqrt(deltaVal)) / (2.0 * aVal)
      val x2 = (-bVal + sqrt(deltaVal)) / (2.0 * aVal)
      Set(x1, x2)
    }
  }
}
