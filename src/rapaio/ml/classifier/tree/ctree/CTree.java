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

package rapaio.ml.classifier.tree.ctree;

import rapaio.data.Frame;
import rapaio.data.Var;
import rapaio.data.VarRange;
import rapaio.ml.classifier.AbstractClassifier;
import rapaio.ml.classifier.CResult;
import rapaio.ml.classifier.tools.DensityVector;
import rapaio.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tree classifier.
 *
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public class CTree extends AbstractClassifier {

    // parameter default values
    int minCount = 1;
    int maxDepth = Integer.MAX_VALUE;

    CTreeTestCounter testCounter = new CTreeTestCounter.MNominalMNumeric();
    CTreeNominalMethod nominalMethod = new CTreeNominalMethod.Full();
    CTreeNumericMethod numericMethod = new CTreeNumericMethod.Binary();
    CTreeTestFunction function = new CTreeTestFunction.InfoGain();
    CTreeSplitter splitter = new CTreeSplitter.RemainsIgnored();
    CTreePredictor predictor = new CTreePredictor.Standard();

    // tree root node
    private CTreeNode root;
    private int rows;

    // static builders

    public static CTree newID3() {
        return new CTree()
                .withTestCounter(new CTreeTestCounter.OneNominalOneNumeric())
                .withMaxDepth(Integer.MAX_VALUE)
                .withSplitter(new CTreeSplitter.RemainsIgnored())
                .withNominalMethod(new CTreeNominalMethod.Full())
                .withNumericMethod(new CTreeNumericMethod.Ignore())
                .withFunction(new CTreeTestFunction.Entropy())
                .withPredictor(new CTreePredictor.Standard());
    }

    public static CTree newC45() {
        return new CTree()
                .withTestCounter(new CTreeTestCounter.OneNominalOneNumeric())
                .withMaxDepth(Integer.MAX_VALUE)
                .withSplitter(new CTreeSplitter.RemainsToAllWeighted())
                .withNominalMethod(new CTreeNominalMethod.Full())
                .withNumericMethod(new CTreeNumericMethod.Binary())
                .withFunction(new CTreeTestFunction.GainRatio())
                .withPredictor(new CTreePredictor.Standard());
    }

    public static CTree newDecisionStump() {
        return new CTree()
                .withMaxDepth(1)
                .withTestCounter(new CTreeTestCounter.OneNominalOneNumeric())
                .withSplitter(new CTreeSplitter.RemainsToAllWeighted())
                .withNominalMethod(new CTreeNominalMethod.Binary())
                .withNumericMethod(new CTreeNumericMethod.Binary())
                .withPredictor(new CTreePredictor.Standard());
    }

    public static CTree newCART() {
        return new CTree()
                .withMaxDepth(Integer.MAX_VALUE)
                .withTestCounter(new CTreeTestCounter.MNominalMNumeric())
                .withSplitter(new CTreeSplitter.RemainsToAllWeighted())
                .withNominalMethod(new CTreeNominalMethod.Binary())
                .withNumericMethod(new CTreeNumericMethod.Binary())
                .withPredictor(new CTreePredictor.Standard());
    }

    @Override
    public CTree newInstance() {
        return (CTree) new CTree()
                .withMinCount(minCount)
                .withMaxDepth(maxDepth)
                .withNominalMethod(nominalMethod.newInstance())
                .withNumericMethod(numericMethod.newInstance())
                .withFunction(function.newInstance())
                .withSplitter(splitter.newInstance())
                .withPredictor(predictor.newInstance())
                .withVarSelector(varSelector.newInstance());
    }

    public CTreeNode getRoot() {
        return root;
    }

    public int getMinCount() {
        return minCount;
    }

    public CTree withMinCount(int minCount) {
        if (minCount < 1) {
            throw new IllegalArgumentException("min cont must be an integer positive number");
        }
        this.minCount = minCount;
        return this;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public CTree withMaxDepth(int maxDepth) {
        if (maxDepth < 1) {
            throw new IllegalArgumentException("max depth must be an integer greater than 0");
        }
        this.maxDepth = maxDepth;
        return this;
    }

    public CTreeTestCounter getCTreeTestCounter() {
        return testCounter;
    }

    public CTree withTestCounter(CTreeTestCounter CTreeTestCounter) {
        this.testCounter = CTreeTestCounter;
        return this;
    }

    public CTreeNominalMethod getNominalMethod() {
        return nominalMethod;
    }

    public CTree withNominalMethod(CTreeNominalMethod methodNominal) {
        this.nominalMethod = methodNominal;
        return this;
    }

    public CTreeNumericMethod getNumericMethod() {
        return numericMethod;
    }

    public CTree withNumericMethod(CTreeNumericMethod numericMethod) {
        this.numericMethod = numericMethod;
        return this;
    }

    public CTreeTestFunction getFunction() {
        return function;
    }

    public CTree withFunction(CTreeTestFunction function) {
        this.function = function;
        return this;
    }

    public CTreeSplitter getSplitter() {
        return splitter;
    }

    public CTree withSplitter(CTreeSplitter splitter) {
        this.splitter = splitter;
        return this;
    }

    public CTreePredictor getPredictor() {
        return predictor;
    }

    public CTree withPredictor(CTreePredictor predictor) {
        this.predictor = predictor;
        return this;
    }

    @Override
    public String name() {
        return "TreeClassifier";
    }

    @Override
    public String fullName() {
        StringBuilder sb = new StringBuilder();
        sb.append("TreeClassifier{");
        sb.append("varSelector=").append(varSelector.name()).append(",");
        sb.append("minCount=").append(minCount).append(",");
        sb.append("maxDepth=").append(maxDepth).append(",");
        sb.append("testCounter=").append(testCounter.name()).append(",");
        sb.append("numericMethod=").append(numericMethod.name()).append(",");
        sb.append("nominalMethod=").append(nominalMethod.name()).append(",");
        sb.append("function=").append(function.name()).append(",");
        sb.append("splitter=").append(splitter.name()).append(",");
        sb.append("predictor=").append(predictor.name());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void learn(Frame df, Var weights, String... targetVars) {

        List<String> targetVarList = new VarRange(targetVars).parseVarNames(df);
        if (targetVarList.isEmpty()) {
            throw new IllegalArgumentException("tree classifier must specify a target variable");
        }
        if (targetVarList.size() > 1) {
            throw new IllegalArgumentException("tree classifier can't fit more than one target variable");
        }
        this.targetNames = targetVarList.toArray(new String[targetVarList.size()]);

        dict = Arrays.stream(this.targetNames).collect(Collectors.toMap(s -> s, s -> df.var(s).dictionary()));
        rows = df.rowCount();

        testCounter.initialize(df, firstTargetName());

        root = new CTreeNode(null, "root", spot -> true);
        root.learn(this, df, weights, maxDepth);
    }

    @Override
    public CResult predict(Frame df, boolean withClasses, boolean withDensities) {

        CResult prediction = CResult.newEmpty(this, df, withClasses, withDensities);
        prediction.addTarget(firstTargetName(), firstDictionary());

        df.stream().forEach(spot -> {
            Pair<Integer, DensityVector> result = predictor.predict(this, spot, root);
            if (withClasses)
                prediction.firstClasses().setIndex(spot.row(), result.first);
            if (withDensities)
                for (int j = 0; j < firstDictionary().length; j++) {
                    prediction.firstDensity().setValue(spot.row(), j, result.second.get(j));
                }
        });
        return prediction;
    }

    @Override
    public void buildSummary(StringBuilder sb) {
        sb.append("> ").append(fullName()).append("\n");

        sb.append(String.format("n=%d\n", rows));

        sb.append("\n");
        sb.append("description:\n");
        sb.append("split, n/err, classes (densities) [* if is leaf]\n\n");

        buildSummary(sb, root, 0);
    }

    private void buildSummary(StringBuilder sb, CTreeNode node, int level) {
        sb.append("|");
        for (int i = 0; i < level; i++) {
            sb.append("   |");
        }
        if (node.getParent() == null) {
            sb.append("root").append(" ");
            sb.append(node.getDensity().sum(true)).append("/");
            sb.append(node.getDensity().sumExcept(node.getBestIndex(), true)).append(" ");
            sb.append(firstDictionary()[node.getBestIndex()]).append(" (");
            DensityVector d = node.getDensity().solidCopy();
            d.normalize(false);
            for (int i = 1; i < firstDictionary().length; i++) {
                sb.append(String.format("%.6f", d.get(i))).append(" ");
            }
            sb.append(") ");
            if (node.isLeaf()) sb.append("*");
            sb.append("\n");

        } else {

            sb.append(node.getGroupName()).append("  ");

            sb.append(node.getDensity().sum(true)).append("/");
            sb.append(node.getDensity().sumExcept(node.getBestIndex(), true)).append(" ");
            sb.append(firstDictionary()[node.getBestIndex()]).append(" (");
            DensityVector d = node.getDensity().solidCopy();
            d.normalize(false);
            for (int i = 1; i < firstDictionary().length; i++) {
                sb.append(String.format("%.6f", d.get(i))).append(" ");
            }
            sb.append(") ");
            if (node.isLeaf()) sb.append("*");
            sb.append("\n");
        }

        // children

        if (!node.isLeaf()) {
            node.getChildren().stream().forEach(child -> buildSummary(sb, child, level + 1));
        }
    }
}