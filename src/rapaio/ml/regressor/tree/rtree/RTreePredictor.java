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

import rapaio.core.stat.Sum;
import rapaio.core.stat.WeightedMean;
import rapaio.data.Numeric;
import rapaio.data.stream.FSpot;
import rapaio.util.Pair;

/**
 * Created by <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a> on 11/24/14.
 */
public interface RTreePredictor {

    String name();

    Pair<Double, Double> predict(RTree tree, FSpot spot, RTreeNode root);

    RTreePredictor STANDARD = new RTreePredictor() {

        @Override
        public String name() {
            return "standard";
        }

        @Override
        public Pair<Double, Double> predict(RTree tree, FSpot spot, RTreeNode node) {

            if (node.isLeaf())
                return new Pair<>(node.getValue(), node.getWeight());

            for (RTreeNode child : node.getChildren()) {
                if (child.getPredicate().test(spot)) {
                    return predict(tree, spot, child);
                }
            }

            Numeric values = Numeric.newEmpty();
            Numeric weights = Numeric.newEmpty();
            for (RTreeNode child : node.getChildren()) {
                Pair<Double, Double> prediction = predict(tree, spot, child);
                values.addValue(prediction.first);
                weights.addValue(prediction.second);
            }
            return new Pair<>(new WeightedMean(values, weights).value(), new Sum(weights).value());
        }
    };
}
