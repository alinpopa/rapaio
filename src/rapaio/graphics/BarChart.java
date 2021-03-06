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

package rapaio.graphics;

import rapaio.data.Index;
import rapaio.data.Nominal;
import rapaio.data.Numeric;
import rapaio.data.Var;
import rapaio.graphics.base.HostFigure;
import rapaio.graphics.base.Range;
import rapaio.graphics.opt.ColorPalette;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class BarChart extends HostFigure {

    private final Var category;
    private final Var condition;
    private final Var numeric;
    private boolean density = false;
    private Range range;
    private int[] sel;
    private double[][] hits;
    private double[] totals;

    public BarChart(Var category) {
        this(category, null);
    }

    public BarChart(Var category, Var condition) {
        this(category, condition, null);
    }

    public BarChart(Var category, Var condition, Var numeric) {
        if (!category.type().isNominal()) {
            throw new IllegalArgumentException("categories are nominal only");
        }
        if (condition == null) {
            condition = Nominal.newEmpty(category.rowCount(), new HashSet<>());
        }
        if (!condition.type().isNominal()) {
            throw new IllegalArgumentException("conditions are nominal only");
        }
        if (numeric == null) {
            numeric = Numeric.newFill(category.rowCount(), 1);
        }
        if (!numeric.type().isNumeric()) {
            throw new IllegalArgumentException("Numeric var must be .. isNumeric");
        }

        this.category = category;
        this.condition = condition;
        this.numeric = numeric;

        leftThick(true);
        leftMarkers(true);
        bottomThick(true);
        bottomMarkers(true);

        int shift = 9;
        color(Index.newSeq(shift, condition.dictionary().length));
    }

    private SortType sort = SortType.NONE;

    public static enum SortType {

        NONE, ASC, DESC
    }

    public BarChart useSortType(SortType sort) {
        this.sort = sort;
        return this;
    }

    private int top = Integer.MAX_VALUE;

    public BarChart useTop(int top) {
        this.top = top;
        return this;
    }

    public void useDensity(boolean density) {
        this.density = density;
    }

    @Override
    public Range buildRange() {
        if (range == null) {

            // learn preliminaries
            int width = category.dictionary().length;
            int height = condition.dictionary().length;

            totals = new double[width];
            hits = new double[width][height];

            int len = Integer.MAX_VALUE;
            len = Math.min(len, category.rowCount());
            len = Math.min(len, condition.rowCount());
            len = Math.min(len, numeric.rowCount());

            for (int i = 0; i < len; i++) {
                hits[category.index(i)][condition.index(i)] += numeric.value(i);
                totals[category.index(i)] += numeric.value(i);
            }

            if (density) {
                double t = 0;
                for (double total : totals) {
                    t += total;
                }
                for (int i = 0; i < hits.length; i++) {
                    for (int j = 0; j < hits[i].length; j++) {
                        hits[i][j] /= t;
                    }
                    totals[i] /= t;
                }
            }

            // now restrict values
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < totals.length; i++) {
                list.add(i);
            }

            Collections.sort(list, (o1, o2) -> {
                if (totals[o1] == totals[o2])
                    return 0;
                int sign = (SortType.ASC.equals(sort)) ? 1 : -1;
                return totals[o1] < totals[o2] ? -sign : sign;
            });
            if (top < list.size()) {
                list = list.subList(0, top);
            }

            sel = new int[list.size()];
            if (SortType.NONE.equals(sort)) {
                Set<Integer> set = new HashSet<>(list);
                int pos = 0;
                for (int i = 0; i < totals.length; i++) {
                    if (set.contains(i)) {
                        sel[pos++] = i;
                    }
                }
            } else {
                for (int i = 0; i < sel.length; i++) {
                    sel[i] = list.get(i);
                }
            }

            // now learn range
            range = new Range();
            for (int aSel1 : sel) {
                range.union(Double.NaN, totals[aSel1]);
                range.union(Double.NaN, 0);
            }
            int cnt = 0;
            for (int aSel : sel) {
                if (totals[aSel] > 0)
                    cnt++;
            }
            range.union(-0.5, 0);
            range.union(cnt - 0.5, 0);
        }
        return range;
    }

    @Override
    public void buildLeftMarkers() {
        buildNumericLeftMarkers();
    }

    @Override
    public void buildBottomMarkers() {
        bottomMarkersPos.clear();
        bottomMarkersMsg.clear();

        int cnt = 0;
        for (int aSel1 : sel) {
            if (totals[aSel1] > 0)
                cnt++;
        }
        int xspots = cnt;
        double xspotwidth = getViewport().width / (1. * xspots);

        cnt = 0;
        for (int aSel : sel) {
            if (totals[aSel] == 0)
                continue;
            bottomMarkersPos.add(xspotwidth * (0.5 + cnt));
            bottomMarkersMsg.add(category.dictionary()[aSel]);
            cnt++;
        }
    }

    @Override
    public void paint(Graphics2D g2d, Rectangle rect) {
        super.paint(g2d, rect);

        int col = 0;
        for (int aSel : sel) {
            if (totals[aSel] == 0)
                continue;

            double ystart = 0;
            for (int j = 0; j < condition.dictionary().length; j++) {
                double yend = ystart + hits[aSel][j];

                int[] x = {
                        (int) xScale(col - 0.4),
                        (int) xScale(col - 0.4),
                        (int) xScale(col + 0.4),
                        (int) xScale(col + 0.4),
                        (int) xScale(col - 0.4)};
                int[] y = {
                        (int) yScale(ystart),
                        (int) yScale(yend),
                        (int) yScale(yend),
                        (int) yScale(ystart),
                        (int) yScale(ystart)};

                g2d.setColor(ColorPalette.STANDARD.getColor(0));
                g2d.drawPolygon(x, y, 4);

                x = new int[]{
                        (int) xScale(col - 0.4) + 1,
                        (int) xScale(col - 0.4) + 1,
                        (int) xScale(col + 0.4),
                        (int) xScale(col + 0.4),
                        (int) xScale(col - 0.4) + 1};
                y = new int[]{
                        (int) yScale(ystart),
                        (int) yScale(yend) + 1,
                        (int) yScale(yend) + 1,
                        (int) yScale(ystart),
                        (int) yScale(ystart)};

                g2d.setColor(getCol(j));
                g2d.fillPolygon(x, y, 4);

                ystart = yend;
            }
            col++;
        }
    }
}
