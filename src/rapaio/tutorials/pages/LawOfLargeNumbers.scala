/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package rapaio.tutorials.pages


/**
 * @author Aurelian Tutuianu
 */
class LawOfLargeNumbers extends TutorialPage {
  def pageName: String = "LawOfLargeNumbers"

  def pageTitle: String = "Explore Law of Large Numbers"

  def render {
    //    RandomSource.setSeed(1)
    //    heading(2, "Simulation on Law of Large Numbers")
    //    p("In the probability theory the Law of the Large Numbers states that when " + "do you repeat an experiment a large number of times, the average " + "of the results of experiment should be close to the expected getValue. " + "The sample average will become closer as more trials are performed.")
    //    p("To illustrate the intuition behind this law we will consider our experiment " + "to be a run of rolls of a dice. A dice has 6 possible outcomes, " + "the integer numbers from 1 to 6, with each output having equal " + "probability. Therefore teh expected getValue of a single die roll is \\( (1+2+3+4+5+6)/6=3.5 \\)")
    //    p("We simulate the event of a single die roll to be a draw of a number from the " + "discrete uniform distribution with minimum getValue equals to 1 and maximum " + "getValue equals to 6. To simulate a large number of independent events we " + "simply draw a large sample of generated random numbers from the same distribution.")
    //    p("Rapaio makes this possible by using the following code:")
    //    code("        final int N = 1_000;\n" + "        Vector dice = new DUniform(1, 6).sample(N);\n")
    //    val N: Int = 1 _000
    //    val events: Nothing = new Nothing(1, 6).sample(N)
    //    p("Thus we have stored in a vector N (1000) outputs of those events. " + "We compute the running mean using StatOnline:")
    //    code("        StatOnline ocs = new StatOnline();\n" + "        Vector mean = new NumericVector(\"mean\", N);\n" + "        for (int i = 0; i < events.getRowCount(); i++) {\n" + "            ocs.update(events.getValue(i), 1);\n" + "            mean.setValue(i, ocs.getMean());\n" + "        }\n")
    //    val ocs: StatOnline = new StatOnline
    //    val mean: Nothing = new Nothing(N)
    //    {
    //      var i: Int = 0
    //      while (i < events.getRowCount) {
    //        {
    //          ocs.update(events.getValue(i))
    //          mean.setValue(i, ocs.getMean)
    //        }
    //        ({
    //          i += 1; i - 1
    //        })
    //      }
    //    }
    //    p("Now we have the running mean stored in the vector mean and we can plot " + "how that running mean evolves as the size of the sample grows.")
    //    draw(new Plot().add(new Nothing(0, 3.5).setLwd(1.5f).setColorIndex(1)).add(new Nothing(Vectors.newSeq(1, N, 1), mean).setLwd(1.5f).setColorIndex(2)).setYRange(2.5, 4.5), 800, 300)
    //    p("Thus we can clearly notice two fact from the plot above. " + "First fact is that the running average gets closer to the " + "expected getValue, as sample size grows. " + "Second fact is that deviation from expected getValue is smaller as " + "the sample size grows aka. smaller variation. ")
    //    p("The code for drawing the plot follows:")
    //    code("        draw(new Plot()\n" + "                .add(new ABLine(0, 3.5).setLwd(1.5f).setColorIndex(1))\n" + "                .add(new Lines(new IndexVector(1, N, 1), mean)\n" + "                        .setLwd(1.5f)\n" + "                        .setColorIndex(2))\n" + "                .setYRange(2.5, 4.5), \n" + "                800, 300);\n" + "")
    //    p(">>>This tutorial is generated with Rapaio document printer facilities.<<<")
  }
}