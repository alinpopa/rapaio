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

package rapaio.data.formula;

import rapaio.core.ColRange;
import rapaio.data.Frame;
import rapaio.data.SolidFrame;
import rapaio.data.Var;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
@Deprecated
public class FFRemoveCols implements FrameFilter {

    private final ColRange colRange;

    public FFRemoveCols(ColRange colRange) {
        this.colRange = colRange;
    }

    @Override
    public Frame apply(Frame df) {
        Set<String> remove = new HashSet<>(colRange.parseColumnNames(df));
        List<Var> vars = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (String varName : df.colNames()) {
            if (remove.contains(varName)) {
                continue;
            }
            vars.add(df.col(varName));
            names.add(varName);
        }
        return new SolidFrame(df.rowCount(), vars, names, df.weights());
    }
}
