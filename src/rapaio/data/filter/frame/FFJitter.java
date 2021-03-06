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

package rapaio.data.filter.frame;

import rapaio.core.distributions.Distribution;
import rapaio.core.distributions.Normal;
import rapaio.data.Frame;

import java.util.List;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> at 12/10/14.
 */
public class FFJitter extends AbstractFF {

    private final Distribution d;

    public FFJitter(String... varNames) {
        this(0.1, varNames);
    }

    public FFJitter(double sd, String... varNames) {
        this(new Normal(0, sd), varNames);
    }

    public FFJitter(Distribution d, String... varNames) {
        super(varNames);
        if (d == null) {
            throw new IllegalArgumentException("distribution parameter cannot be empty");
        }
        this.d = d;
    }

    @Override
    public void fit(Frame df) {
        checkRangeVars(1, df.varCount(), df, varNames);
    }

    @Override
    public Frame apply(Frame df) {

        List<String> names = parse(df, varNames);

        for (int i = 0; i < df.rowCount(); i++) {
            for (String varName : names) {
                df.setValue(i, varName, df.value(i, varName) + d.sampleNext());
            }
        }
        return df;
    }
}
