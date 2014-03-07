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

//import rapaio.core.distributions.Normal;
//import rapaio.core.distributions.StudentT;
//import rapaio.graphics.Plot;
//import rapaio.graphics.plot.FunctionLine;

import rapaio.workspace.Workspace._

/**
 * @author Aurelian Tutuianu
 */
class StudentTDistribution extends TutorialPage {
  def pageName: String = "StudentTDistribution"

  def pageTitle: String = "Short story of a distribution: Student's T"

  def render() {
    heading(1, "Short story of a distribution: Student's T")
    heading(3, "Foreword")
    p("I have always believed that knowing the history of scientific " + "achievements is a must for anybody. This story is about " + "William Sealy Gosset and it's famous t distribution. ")
    heading(3, "The context")
    p("When the story begins, the science of mathematical statistics " + "lived its first years as a serious scientific discipline, " + "mainly through the efforts made by Karl Pearson, who was " + "the founder of the first university statistics department " + "in the world. ")
    p("After graduating on chemistry and mathematics at New College, Oxford, " + "Gosset joined the brewery of Arthur Guinness and Son, and " + "started to apply his statistical knowledge in brewery and on " + "the selection of barley. ")
    p("Note that at that time the methods available involved mainly " + "the practice of Karl Pearson and some other eminent statisticians, " + "around the normal distribution and Central Limit Theorem. ")
    heading(3, "Probability density and central limit theorem")
    p("The probability density of a standard normal distribution " + "looks like this: ")
    p("To understand it's mechanics you have to imagine a " + "process, which produces a numeric getValue. " + "If the only thing you would know about the getValue of the " + "number to be produces is it's range, than you don't know " + "much. ")
    p("Statistics adds a new dimension to that range: " + "your expectation, aka. \"which values are expected to be produced with " + "a high probability, and which not\". ")
    p("The standard normal distribution from the above tells us, for example, " + "that we expect that the getValue produced by our process to be very close " + "to 0. It might variate but only around 0. There is many information contained " + "in this density function like we expect that the getValue produced to be " + "in interval [-2,+2] in most of the cases. ")
    p("Central Limit Theorem " + "says something like: \"if you repeat the event enough times independently and " + "in the same conditions, the mean of the values produced " + "will follow a normal distribution\". ")
    p("This theory is wonderful due to its generality. However it has a fundamental flow: " + "it doesn't work well if you don't repeat the event many times. ")
    p("For scientists like Pearson that was not a problem, since they managed in many cases " + "to get enough data. But for Gosset that was a big trouble. It is not easy to see " + "that you can't repeat the event of barley selection many times. That " + "barley have to be selected, have to grow and be measured after harvest. It was too costly " + "to repeat the experiments enough time. ")
    p(">>>This tutorial is generated with Rapaio document printer facilities.<<<")
  }
}