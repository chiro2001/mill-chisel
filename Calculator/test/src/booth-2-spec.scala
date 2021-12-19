package Calculator

import chiseltest._
import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage
import org.scalatest.{Matchers, FlatSpec}

import scala.math.pow

import Calculator._

class Booth2Test extends FlatSpec with ChiselScalatestTester with Matchers {
    behavior of ""

    it should "work" in {
        test(new Booth2) { c =>
            c.reset.poke(true.B)
            c.clock.step()
            c.reset.poke(false.B)
            c.io.in.op_1.poke(15.U)
            c.io.in.op_2.poke(15.U)
            c.io.in.start.poke(true.B)
            c.clock.step()
            c.io.in.start.poke(false.B)
            while (c.io.out.end.peek().litValue != 1) {
                c.clock.step()
            }

            c.io.out.result.expect(225.U)

            //reset
            c.reset.poke(true.B)
            c.clock.step()
            c.reset.poke(false.B)

            val INT_MAX = pow(2, 32).toLong - 1
            // val INT_MAX = 16

            val r = scala.util.Random

            for (i <- 0 until 10000) {
                val random_op_1 = r.nextLong.abs % (INT_MAX + 1)
                val random_op_2 = r.nextLong.abs % (INT_MAX + 1)
                var random_result =
                    (random_op_1 * random_op_2) % (INT_MAX + 1)

                if (random_result < 0) {
                    random_result += (INT_MAX + 1)
                }

                println(
                  "let's test " + random_op_1 + "*" + random_op_2 + "=" + random_result
                )
                c.io.in.op_1.poke(random_op_1.asUInt(32.W))
                c.io.in.op_2.poke(random_op_2.asUInt(32.W))
                c.io.in.start.poke(true.B)
                c.clock.step()
                c.io.in.start.poke(false.B)
                while (c.io.out.end.peek().litValue != 1) {
                    c.clock.step()
                }
                c.io.out.result
                    .expect(random_result.asUInt(32.W))

                c.clock.step()
            }

        }
    }
}
