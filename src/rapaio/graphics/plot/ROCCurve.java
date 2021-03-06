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

package rapaio.graphics.plot;

import rapaio.graphics.Plot;
import rapaio.graphics.base.Range;
import rapaio.graphics.opt.ColorPalette;
import rapaio.ml.eval.ROC;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Aurelian Tutuianu <paderati@yahoo.com>
 */
public class ROCCurve extends PlotComponent {

    private final ROC roc;

    public ROCCurve(ROC roc) {
        this.roc = roc;
    }

    @Override
    public Range buildRange() {
        return new Range(0, 0, 1, 1);
    }

    @Override
    public void initialize(Plot parent) {
        super.initialize(parent);
        parent.xLab("fp rate");
        parent.yLab("tp rate");
    }

    @Override
    public void paint(Graphics2D g2d) {
        g2d.setColor(getCol(0));
        g2d.setStroke(new BasicStroke(getLwd()));
        g2d.setBackground(ColorPalette.STANDARD.getColor(255));

        for (int i = 1; i < roc.getData().rowCount(); i++) {
            g2d.setColor(getCol(i));
            double x1 = parent.xScale(roc.getData().value(i - 1, "fpr"));
            double y1 = parent.yScale(roc.getData().value(i - 1, "tpr"));
            double x2 = parent.xScale(roc.getData().value(i, "fpr"));
            double y2 = parent.yScale(roc.getData().value(i, "tpr"));

            if (parent.getRange().contains(
                    roc.getData().value(i - 1, "fpr"),
                    roc.getData().value(i - 1, "tpr")
            )
                    && parent.getRange().contains(
                    roc.getData().value(i, "fpr"),
                    roc.getData().value(i, "tpr"))) {
                g2d.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }

        g2d.setColor(getCol(0));
    }
}
