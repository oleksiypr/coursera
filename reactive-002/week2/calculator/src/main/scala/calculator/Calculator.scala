package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {
    ???
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = expr match {
    case Literal(v)    => v
    case Plus   (a, b) => eval(a, references) + eval(b, references)
    case Minus  (a, b) => eval(a, references) - eval(b, references)
    case Times  (a, b) => eval(a, references) * eval(b, references) 
    case Divide (a, b) => eval(a, references) / eval(b, references)
    case Ref    (name) => {
      def assertCyclic(ref: Expr, passed: Set[String]): Unit = ref match {
        case Ref(r) => {
          assert(!passed.contains(r))
          assertCyclic(getReferenceExpr(r, references), passed + r)
        }
        case _ => ()
      }
      val next = getReferenceExpr(name, references)
      assertCyclic(next, Set(name))
      eval(next, references - name)
    }
  }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
