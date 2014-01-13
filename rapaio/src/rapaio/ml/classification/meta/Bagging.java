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
package rapaio.ml.classification.meta;

import rapaio.data.Frame;
import rapaio.data.Frames;
import rapaio.data.NomVector;
import rapaio.ml.classification.AbstractClassifier;
import rapaio.ml.classification.Classifier;
import rapaio.sample.StatSampling;

import java.util.ArrayList;
import java.util.List;

/**
 * User: <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
@Deprecated
public class Bagging extends AbstractClassifier<Bagging> {

    private final double p;
    private final int bags;
    private final Classifier provider;
    private List<Classifier> classifiers = new ArrayList<>();
    private String[] dict;
    private String classColName;
    private NomVector pred;
    private Frame dist;

    public Bagging(double p, int bags, Classifier provider) {
        this.p = p;
        this.bags = bags;
        this.provider = provider;
    }

    public Bagging newInstance() {
        Bagging bag = new Bagging(p, bags, provider);
        return bag;
    }

    @Override
    public void learn(Frame df, List<Double> weights, String classColName) {
        this.classColName = classColName;
        this.dict = df.getCol(classColName).getDictionary();
        classifiers.clear();
        for (int i = 0; i < bags; i++) {
            Frame bootstrap = StatSampling.randomBootstrap(df, (int) Math.rint(df.getRowCount() * p));
            Classifier c = provider.newInstance();
            c.learn(bootstrap, classColName);
            classifiers.add(c);
        }
    }

    @Override
    public void predict(final Frame df) {
        // voting

        pred = new NomVector(df.getRowCount(), dict);
        dist = Frames.newMatrixFrame(df.getRowCount(), dict);

        // collect results from each classifier
        for (Classifier c : classifiers) {
            c.predict(df);
            for (int j = 0; j < df.getRowCount(); j++) {
                String prediction = c.getPrediction().getLabel(j);
                double prev = dist.getValue(j, dist.getColIndex(prediction));
                if (prev != prev) {
                    prev = 0;
                }
                dist.setValue(j, dist.getColIndex(prediction), prev + 1);
            }
        }
        for (int i = 0; i < dist.getRowCount(); i++) {
            int index = -1;
            double max = -1;
            for (int j = 0; j < dist.getColCount(); j++) {
                double freq = dist.getValue(i, j);
                dist.setValue(i, j, freq / (1. * classifiers.size()));
                if (max < freq) {
                    max = freq;
                    index = j;
                }
            }
            pred.setLabel(i, pred.getDictionary()[index + 1]);
        }
    }

    @Override
    public NomVector getPrediction() {
        return pred;
    }

    @Override
    public Frame getDistribution() {
        return dist;
    }

    @Override
    public void summary() {
    }
}