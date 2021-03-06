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

import rapaio.data.Frame;
import rapaio.data.VarRange;

import java.util.List;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> at 12/5/14.
 */
public class FFRemoveVars extends AbstractFF {

    public FFRemoveVars(String... varNames) {
        super(varNames);
    }

    @Override
    public void fit(Frame df) {
    }

    @Override
    public Frame apply(Frame df) {
        checkRangeVars(0, df.varCount() - 1, df, varNames);

        List<String> names = parse(df, varNames);
        return df.removeVars(new VarRange(names.toArray(new String[names.size()])));
    }
}
