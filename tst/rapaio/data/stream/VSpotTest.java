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

package rapaio.data.stream;

import org.junit.Test;
import rapaio.core.stat.Sum;
import rapaio.data.Numeric;
import rapaio.data.Var;

import static junit.framework.Assert.assertEquals;

/**
 * Created by <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>.
 */
public class VSpotTest {

    @Test
    public void testNumericStream() {
        Var x = Numeric.newWrapOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Var y = x.solidCopy().stream().transValue(Math::sqrt).toMappedVar();

        double v = 0;
        for (int i = 0; i < 10; i++) {
            v += Math.sqrt(x.value(i));
        }
        assertEquals(v, new Sum(y).value(), 1e-12);
    }
}
