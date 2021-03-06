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

package rapaio.core.distributions.empirical;

/**
 * User: <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class KFuncTriweight implements KFunc {

    @Override
    public double pdf(double x, double x0, double bandwidth) {
        double value = Math.abs(x - x0) / bandwidth;
        if (value <= 1) {
            double weight = 1 - value * value;
            return 35. * weight * weight * weight / 32.;
        }
        return 0;
    }

    @Override
    public double getMinValue(double x0, double bandwidth) {
        return x0 - bandwidth;
    }

    @Override
    public double getMaxValue(double x0, double bandwidth) {
        return x0 + bandwidth;
    }
}
