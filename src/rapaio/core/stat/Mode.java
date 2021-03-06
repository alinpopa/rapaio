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

package rapaio.core.stat;

import rapaio.core.Printable;
import rapaio.data.Var;

import java.util.Arrays;

/**
 * User: <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class Mode implements Printable {

    private final String varName;
    private final boolean includeMissing;
    private final String[] modes;

    public Mode(Var var, boolean includeMissing) {
        this.varName = var.name();
        this.includeMissing = includeMissing;
        this.modes = compute(var);
    }

    private String[] compute(Var var) {
        if (!var.type().isNominal()) {
            throw new IllegalArgumentException("Can't compute mode for other than nominal vectors");
        }
        int[] freq = new int[var.dictionary().length];
        for (int i = 0; i < var.rowCount(); i++) {
            if (var.missing(i)) {
                continue;
            }
            freq[var.index(i)]++;
        }
        int max = 0;
        int start = includeMissing ? 0 : 1;
        for (int i = start; i < freq.length; i++) {
            max = Math.max(max, freq[i]);
        }
        int count = 0;
        for (int i = start; i < freq.length; i++) {
            if (freq[i] == max) {
                count++;
            }
        }
        int pos = 0;
        String[] modes = new String[count];
        for (int i = start; i < freq.length; i++) {
            if (freq[i] == max) {
                modes[pos++] = var.dictionary()[i];
            }
        }
        return modes;
    }

    public String[] values() {
        return modes;
    }

    @Override
    public void buildSummary(StringBuilder sb) {
        sb.append(String.format("mode[%s]\n%s", varName, Arrays.deepToString(modes)));
    }
}
