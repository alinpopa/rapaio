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

import rapaio.data.Var;
import rapaio.graphics.Plot;
import rapaio.graphics.base.Range;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> at 12/5/14.
 */
public class Histogram2D extends PlotComponent {

    private final Var x;
    private final Var y;
    private int xBins = 10;
    private int yBins = 10;
    private int[][] freq;
    private int maxFreq;

    public Histogram2D(Var x, Var y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void initialize(Plot parent) {
        super.initialize(parent);
        parent.leftMarkers(true);
        parent.leftThick(true);
        parent.xLab(x.name());
        parent.bottomMarkers(true);
        parent.bottomThick(true);
        parent.yLab(y.name());
    }

    public Histogram2D withXBins(int xBins) {
        this.xBins = xBins;
        return this;
    }

    public Histogram2D withYBins(int yBins) {
        this.yBins = yBins;
        return this;
    }

    @Override
    protected Range buildRange() {
        if (x.rowCount() == 0) {
            return null;
        }
        Range range = new Range();
        for (int i = 0; i < Math.min(x.rowCount(), y.rowCount()); i++) {
            if (x.missing(i) || y.missing(i)) {
                continue;
            }
            range.union(x.value(i), y.value(i));
        }
        return range;
    }

    private void computeData() {
        Range range = buildRange();
        double w = range.width() / xBins;
        double h = range.height() / yBins;

        freq = new int[xBins][yBins];

        for (int i = 0; i < Math.min(x.rowCount(), y.rowCount()); i++) {
            if (x.missing(i) || y.missing(i))
                continue;

            int xx = Math.min(xBins - 1, (int) Math.floor((x.value(i) - range.x1()) / w));
            int yy = Math.min(yBins - 1, (int) Math.floor((y.value(i) - range.y1()) / h));
            freq[xx][yy]++;
            if (maxFreq < freq[xx][yy]) {
                maxFreq = freq[xx][yy];
            }
        }
    }

    @Override
    public void paint(Graphics2D g2d) {

        computeData();

        // paint each rectangle as a blue gradient

        Range range = buildRange();
        double w = range.width() / xBins;
        double h = range.height() / yBins;

        for (int i = 0; i < xBins; i++) {
            for (int j = 0; j < yBins; j++) {
                int blue = (int) (255 * freq[i][j] / (1.0 * maxFreq));
                Color color = new Color(getCol(0).getRed(), getCol(0).getGreen(), getCol(0).getBlue(), blue);
                g2d.setColor(color);
                Rectangle2D.Double rr = new Rectangle2D.Double(
                        parent.xScale(range.x1() + w * i),
                        parent.yScale(range.y1() + h * j + h),
                        parent.xScale(range.x1() + w * i + w) - parent.xScale(range.x1() + w * i),
                        parent.yScale(range.y1() + h * j) - parent.yScale(range.y1() + h * j + h));
                g2d.fill(rr);
            }
        }
    }
}
