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

package rapaio.core.correlation;

import rapaio.core.Printable;
import rapaio.data.*;
import rapaio.data.filter.var.VFRefSort;
import rapaio.printer.Printer;

import java.util.Arrays;

import static rapaio.WS.getPrinter;

/**
 * Spearman's rank correlation coefficient.
 * <p>
 * You can compute coefficient for multiple vectors at the same time.
 * <p>
 * See: http://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient
 * <p>
 * User: <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class RhoCorr implements Printable {

    private final String[] names;
    private final Var[] vars;
    private final double[][] rho;

    public RhoCorr(Var... vars) {
        this.names = new String[vars.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = "V" + i;
        }
        this.vars = vars;
        this.rho = compute();
    }

    public RhoCorr(Frame df) {
        this.names = df.varNames();
        this.vars = new Var[df.varCount()];
        for (int i = 0; i < df.varCount(); i++) {
            vars[i] = df.var(i);
        }
        this.rho = compute();
    }

    private double[][] compute() {
        Var[] sorted = new Var[vars.length];
        Var[] ranks = new Var[vars.length];
        for (int i = 0; i < sorted.length; i++) {
            Index index = Index.newEmpty();
            for (int j = 0; j < vars[i].rowCount(); j++) {
                index.addIndex(j);
            }
            sorted[i] = new VFRefSort(RowComparators.numeric(vars[i], true)).fitApply(index);
            ranks[i] = Numeric.newFill(vars[i].rowCount());
        }

        // compute ranks
        for (int i = 0; i < sorted.length; i++) {
            int start = 0;
            while (start < sorted[i].rowCount()) {
                int end = start;
                while (end < sorted[i].rowCount() - 1 && sorted[i].value(end) == sorted[i].value(end + 1)) {
                    end++;
                }
                double value = 1 + (start + end) / 2.;
                for (int j = start; j <= end; j++) {
                    ranks[i].setValue(sorted[i].index(j), value);
                }
                start = end + 1;
            }
        }

        // compute Pearson on ranks
        return new PearsonRCorrelation(ranks).values();
    }

    public double[][] values() {
        return rho;
    }

    @Override
    public void buildSummary(StringBuilder sb) {
        switch (vars.length) {
            case 1:
                summaryOne(sb);
                break;
            case 2:
                summaryTwo(sb);
                break;
            default:
                summaryMore(sb);
        }
    }

    private void summaryOne(StringBuilder sb) {
        sb.append(String.format("spearman[%s] - Spearman's rank correlation coefficient\n", names[0]));
        sb.append("1\n");
        sb.append("spearman's rank correlation is 1 for identical vectors\n");
    }

    private void summaryTwo(StringBuilder sb) {
        sb.append(String.format("spearman[%s, %s] - Spearman's rank correlation coefficient\n",
                names[0], names[1]));
        sb.append(Printer.formatDecShort.format(rho[0][1])).append("\n");
    }

    private void summaryMore(StringBuilder sb) {
        sb.append(String.format("spearman[%s] - Spearman's rank correlation coefficient\n",
                Arrays.deepToString(names)));

        String[][] table = new String[vars.length + 1][vars.length + 1];
        table[0][0] = "";
        for (int i = 1; i < vars.length + 1; i++) {
            table[0][i] = i + ".";
            table[i][0] = i + "." + names[i - 1];
            for (int j = 1; j < vars.length + 1; j++) {
                table[i][j] = Printer.formatDecShort.format(rho[i - 1][j - 1]);
                if (i == j) {
                    table[i][j] = "x";
                }
            }
        }

        int width = getPrinter().getTextWidth();
        int start = 0;
        int end = start;
        int[] ws = new int[table[0].length];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                ws[i] = Math.max(ws[i], table[i][j].length());
            }
        }
        while (start < vars.length + 1) {
            int w = 0;
            while ((end < (table[0].length - 1)) && ws[end + 1] + w + 1 < width) {
                w += ws[end + 1] + 1;
                end++;
            }
            for (int j = 0; j < table.length; j++) {
                for (int i = start; i <= end; i++) {
                    sb.append(String.format("%" + ws[i] + "s", table[i][j])).append(" ");
                }
                sb.append("\n");
            }
            start = end + 1;
        }
    }
}
