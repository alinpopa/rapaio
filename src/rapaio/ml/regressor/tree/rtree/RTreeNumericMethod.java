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

package rapaio.ml.regressor.tree.rtree;

import rapaio.core.RandomSource;
import rapaio.core.stat.StatOnline;
import rapaio.data.Frame;
import rapaio.data.Index;
import rapaio.data.RowComparators;
import rapaio.data.Var;
import rapaio.data.filters.VFRefSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a> on 11/24/14.
 */
public interface RTreeNumericMethod {

    String name();

    List<RTreeCandidate> computeCandidates(RTree c, Frame df, Var weights, String testColName, String targetColName, RTreeTestFunction function);

    RTreeNumericMethod IGNORE = new RTreeNumericMethod() {
        @Override
        public String name() {
            return "IGNORE";
        }

        @Override
        public List<RTreeCandidate> computeCandidates(RTree c, Frame df, Var weights, String testColName, String targetColName, RTreeTestFunction function) {
            return new ArrayList<>();
        }
    };

    RTreeNumericMethod BINARY = new RTreeNumericMethod() {
        @Override
        public String name() {
            return "BINARY";
        }

        @Override
        public List<RTreeCandidate> computeCandidates(RTree c, Frame df, Var weights, String testColName, String targetColName, RTreeTestFunction function) {
            Var test = df.var(testColName);
            Var target = df.var(targetColName);

            Var sort = new VFRefSort(RowComparators.numeric(test, true)).fitApply(Index.newSeq(df.rowCount()));

            double[] leftVar = new double[df.rowCount()];
            double[] rightVar = new double[df.rowCount()];

            StatOnline so = new StatOnline();

            for (int i = 0; i < df.rowCount(); i++) {
                int row = sort.index(i);
                if (target.missing(row)) {
                    continue;
                }
                so.update(target.value(row), weights.value(row));
                leftVar[row] = so.variance();
            }
            for (int i = df.rowCount() - 1; i >= 0; i--) {
                int row = sort.index(i);
                if (target.missing(row)) {
                    continue;
                }
                so.update(target.value(row), weights.value(row));
                rightVar[row] += so.variance();
            }

            RTreeCandidate best = null;

            for (int i = 0; i < df.rowCount(); i++) {
                int row = sort.index(i);

                if (test.missing(row)) continue;
                if (i < c.minCount || i > df.rowCount() - 1 - c.minCount) continue;
                if (test.value(sort.index(i)) == test.value(sort.index(i + 1))) continue;

                double left = leftVar[i];
                double right = rightVar[i];

                double value = c.function.computeTestValue(left, right);

                RTreeCandidate current = new RTreeCandidate(value, testColName);
                if (best == null) {
                    best = current;

                    final double testValue = test.value(sort.index(i));
                    current.addGroup(
                            String.format("%s <= %.6f", testColName, testValue),
                            spot -> !spot.missing(testColName) && spot.value(testColName) <= testValue);
                    current.addGroup(
                            String.format("%s > %.6f", testColName, testValue),
                            spot -> !spot.missing(testColName) && spot.value(testColName) > testValue);
                } else {
                    int comp = best.compareTo(current);
                    if (comp < 0) continue;
                    if (comp == 0 && RandomSource.nextDouble() > 0.5) continue;
                    best = current;

                    final double testValue = test.value(sort.index(i));
                    current.addGroup(
                            String.format("%s <= %.6f", testColName, testValue),
                            spot -> !spot.missing(testColName) && spot.value(testColName) <= testValue);
                    current.addGroup(
                            String.format("%s > %.6f", testColName, testValue),
                            spot -> !spot.missing(testColName) && spot.value(testColName) > testValue);
                }
            }
            return (best != null) ? Arrays.asList(best) : Collections.EMPTY_LIST;
        }
    };
}
