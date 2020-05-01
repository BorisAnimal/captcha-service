package captchaServer.domain.captcha

import cats.syntax.option._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AnswerGeneratorSpec extends AnyFlatSpec with Matchers {
  "AnswerGenerator " should "Generate sequence of correct length" in {
    for (i <- 1 to 10)
      yield AnswerGenerator(Set("a"), questionLen = i, randomSeed = None).generateAnswer.size shouldBe i
  }

  ".." should "Generate sequence of different characters" in {
    import AnswerGeneratorSpec._
    val ans = gen.generateAnswer

    val res: Vector[Boolean] = chars.toVector.map(c => ans.count(_ == c) < len)

    res.reduce(_ && _) shouldBe true
  }

  ".." should "randomSeed must make sense for result of generation" in {
    import AnswerGeneratorSpec._

    val gen1 = gen
    val gen2 = AnswerGenerator(chars, questionLen = len, randomSeed = 0.some)
    val gen3 = AnswerGenerator(chars, questionLen = len, randomSeed = None)

    val a1 = gen1.generateAnswer
    val a2 = gen2.generateAnswer
    val a3 = gen3.generateAnswer

    val bools = Seq(a1.equals(a2), a1.equals(a3), a2.equals(a3))

    bools.reduce(_ && _) shouldBe false
  }

  ".." should "generate different answers" in {
    import AnswerGeneratorSpec._

    val ans = (0 to 10).map(gen.generateAnswer)

    val compares = (ans zip ans.tail).map {
      case (s1, s2) => s1.equals(s2)
      case _ => true
    }

    compares.reduce(_&&_) shouldBe false
  }
}

object AnswerGeneratorSpec {
  val chars: Set[String] = Set("a", "b", "c")
  val len = 100
  val gen: AnswerGenerator = AnswerGenerator(chars, questionLen = len, randomSeed = 42.some)
}
