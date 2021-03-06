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

package rapaio.ml.classifier.tools;

import rapaio.data.Var;

import java.io.Serializable;
import java.util.Arrays;

import static rapaio.core.MathBase.log2;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public final class DensityTable implements Serializable {

    public static final String[] NUMERIC_DEFAULT_LABELS = new String[]{"?", "less-equals", "greater"};

    private final String[] testLabels;
    private final String[] targetLabels;

    // table with frequencies
    private final double[][] values;

    /**
     * Builds a table with given test columns and target columns
     *
     * @param testLabels   test labels for rows
     * @param targetLabels target labels for columns
     */
    public DensityTable(String[] testLabels, String[] targetLabels) {
        this.testLabels = testLabels;
        this.targetLabels = targetLabels;
        values = new double[testLabels.length][targetLabels.length];
    }

    public DensityTable(Var test, Var target) {
        this(test, target, null);
    }

    /**
     * Builds a density table from two nominal vectors.
     * If not null, weights are used instead of counts.
     *
     * @param test    test var
     * @param target  target var
     * @param weights weights used instead of counts, if not null
     */
    public DensityTable(Var test, Var target, Var weights) {
        this(test.dictionary(), target.dictionary());

        if (!test.type().isNominal()) throw new IllegalArgumentException("test var must be nominal");
        if (!target.type().isNominal()) throw new IllegalArgumentException("target var is not nominal");
        if (test.rowCount() != target.rowCount())
            throw new IllegalArgumentException("test and target must have same row count");

        for (int i = 0; i < test.rowCount(); i++) {
            update(test.index(i), target.index(i), weights != null ? weights.value(i) : 1);
        }
    }

    /**
     * Builds a density table with a binary split, from two nominal vectors.
     * The first row contains instances which have test label equal with given testLabel,
     * second row contains frequencies for the rest of the instances.
     *
     * @param test      test var
     * @param target    target var
     * @param weights   if not null, weights used instead of counts
     * @param testLabel test label used for binary split
     */
    public DensityTable(Var test, Var target, Var weights, String testLabel) {
        this(new String[]{"?", testLabel, "other"}, target.dictionary());

        if (!test.type().isNominal()) throw new IllegalArgumentException("test var must be nominal");
        if (!target.type().isNominal()) throw new IllegalArgumentException("target var is not nominal");
        if (test.rowCount() != target.rowCount())
            throw new IllegalArgumentException("test and target must have same row count");

        for (int i = 0; i < test.rowCount(); i++) {
            int index = 0;
            if (!test.missing(i)) {
                index = (test.label(i).equals(testLabel)) ? 1 : 2;
            }
            update(index, target.index(i), weights != null ? weights.value(i) : 1);
        }
    }

    public void reset() {
        for (double[] line : values) Arrays.fill(line, 0, line.length, 0);
    }

    public void update(int row, int col, double weight) {
        values[row][col] += weight;
    }

    public void move(int row1, int row2, int col, double weight) {
        update(row1, col, -weight);
        update(row2, col, weight);
    }

    public double getTargetEntropy() {
        return getTargetEntropy(false);
    }

    public double getTargetEntropy(boolean useMissing) {
        double entropy = 0;
        double[] totals = new double[targetLabels.length];
        for (int i = 1; i < testLabels.length; i++) {
            for (int j = 1; j < targetLabels.length; j++) {
                totals[j] += values[i][j];
            }
        }
        double total = 0;
        for (int i = 1; i < totals.length; i++) {
            total += totals[i];
        }
        for (int i = 1; i < totals.length; i++) {
            if (totals[i] > 0) {
                entropy += -log2(totals[i] / total) * totals[i] / total;
            }
        }
        double factor = 1.;
        if (useMissing) {
            double missing = 0;
            for (int i = 1; i < targetLabels.length; i++) {
                missing += values[0][i];
            }
            factor = total / (missing + total);
        }
        return factor * entropy;
    }

    public double getSplitEntropy() {
        return getSplitEntropy(false);
    }

    public double getSplitEntropy(boolean useMissing) {
        double[] totals = new double[testLabels.length];
        for (int i = 0; i < testLabels.length; i++) {
            for (int j = 0; j < targetLabels.length; j++) {
                totals[i] += values[i][j];
            }
        }
        double total = 0;
        for (int i = 1; i < totals.length; i++) {
            total += totals[i];
        }
        double gain = 0;
        for (int i = 1; i < testLabels.length; i++) {
            for (int j = 1; j < targetLabels.length; j++) {
                if (values[i][j] > 0)
                    gain += -log2(values[i][j] / totals[i]) * values[i][j] / total;
            }
        }
        if (useMissing) {
            double missing = 0;
            for (int i = 0; i < targetLabels.length; i++) {
                missing += values[0][i];
            }
            return gain * total / (missing + total);
        }
        return gain;
    }

    public double getInfoGain() {
        return getInfoGain(false);
    }

    public double getInfoGain(boolean useMissing) {
        return getTargetEntropy(useMissing) - getSplitEntropy(useMissing);
    }

    public double getSplitInfo() {
        return getSplitInfo(false);
    }

    public double getSplitInfo(boolean useMissing) {
        int start = useMissing ? 0 : 1;
        double[] totals = new double[testLabels.length];
        for (int i = start; i < testLabels.length; i++) {
            for (int j = 1; j < targetLabels.length; j++) {
                totals[i] += values[i][j];
            }
        }
        double total = 0;
        for (int i = start; i < totals.length; i++) {
            total += totals[i];
        }
        double splitInfo = 0;
        for (int i = start; i < totals.length; i++) {
            if (totals[i] > 0) {
                splitInfo += -log2(totals[i] / total) * totals[i] / total;
            }
        }
        return splitInfo;
    }

    public double getGainRatio() {
        return getGainRatio(false);
    }

    public double getGainRatio(boolean useMissing) {
        return getInfoGain(useMissing) / getSplitInfo(useMissing);
    }

    /**
     * Computes the number of columns which have totals equal or greater than minWeight
     *
     * @param useMissing if on counting the missing row is used
     * @return number of columns which meet criteria
     */
    public int countWithMinimum(boolean useMissing, double minWeight) {
        int start = useMissing ? 0 : 1;
        double[] totals = new double[testLabels.length];
        for (int i = start; i < testLabels.length; i++) {
            for (int j = 1; j < targetLabels.length; j++) {
                totals[i] += values[i][j];
            }
        }
        int count = 0;
        for (int i = 1; i < totals.length; i++) {
            if (totals[i] >= minWeight) {
                count++;
            }
        }
        return count;
    }

    public double getGiniIndex() {
        return getGiniIndex(false);
    }

    private double getGiniIndex(boolean useMissing) {
        int start = useMissing ? 0 : 1;
        double[] testTotals = new double[testLabels.length];
        double[] targetTotals = new double[targetLabels.length];
        for (int i = start; i < testLabels.length; i++) {
            for (int j = 0; j < targetLabels.length; j++) {
                testTotals[i] += values[i][j];
                targetTotals[j] += values[i][j];
            }
        }
        double testTotal = 0;
        double targetTotal = 0;
        for (int i = start; i < testLabels.length; i++) {
            testTotal += testTotals[i];
        }
        for (int i = start; i < targetLabels.length; i++) {
            targetTotal += targetTotals[i];
        }

        double gini = 1;
        for (int i = start; i < targetLabels.length; i++) {
            if (targetTotal != 0)
                gini -= Math.pow(targetTotals[i] / targetTotal, 2);
        }

        for (int i = start; i < testLabels.length; i++) {
            double gini_k = 1;
            for (int j = start; j < targetLabels.length; j++) {
                if (testTotals[i] != 0)
                    gini_k -= Math.pow(values[i][j] / testTotals[i], 2);
            }
            if (testTotal != 0)
                gini -= gini_k * testTotals[i] / testTotal;
        }
        return gini;
    }
}
