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

package rapaio.data.matrix;

import rapaio.data.Numeric;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class Matrix {

    final int m; // row count
    final int n; // col count
    final List<Numeric> data; // matrices are stored as column vectors

    public Matrix(int m, int n) {
        this.m = m;
        this.n = n;
        data = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            data.add(Numeric.newFill(m));
        }
    }

    public Matrix(Numeric... vectors) {
        m = vectors[0].rowCount();
        n = vectors.length;
        data = new ArrayList<>(Arrays.asList(vectors).subList(0, n));
    }

    public Matrix(List<Numeric> vectors) {
        m = vectors.get(0).rowCount();
        n = vectors.size();
        data = new ArrayList<>(vectors);
    }

    public Matrix(int step, double[] values) {
        int len = values.length;
        this.m = len / step;
        this.n = len / m;
        if (m * n != len) {
            throw new IllegalArgumentException("step does not fit the length of values");
        }

        data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            data.add(Numeric.newEmpty());
        }
        int pos = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                data.get(j).addValue(values[pos++]);
            }
        }
    }

    public Matrix(double[][] source) {
        this(source, 0, source.length - 1, 0, source[0].length - 1);
    }

    public Matrix(double[][] source, int mFirst, int mLast, int nFirst, int nLast) {
        m = mLast - mFirst + 1;
        n = nLast - nFirst + 1;
        data = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            data.add(Numeric.newFill(m));
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                set(i, j, source[i + mFirst][j + nFirst]);
            }
        }
    }

    /**
     * Get a submatrix.
     *
     * @param r  Array of row indices.
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(r(:), j0:j1)
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */

    public Matrix getMatrix(int[] r, int j0, int j1) {
        Matrix B = new Matrix(r.length, j1 - j0 + 1);
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = j0; j <= j1; j++) {
                    B.set(i, j - j0, get(r[i], j));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return B;
    }

    public double get(int i, int j) {
        return data.get(j).value(i);
    }

    public void set(int i, int j, double x) {
        data.get(j).setValue(i, x);
    }

    public int getRows() {
        return m;
    }

    public int getCols() {
        return n;
    }

    /**
     * Copy the internal two-dimensional array.
     *
     * @return Two-dimensional array copy of rapaio.data.matrix elements.
     */
    public double[][] getArrayCopy() {
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = get(i, j);
            }
        }
        return C;
    }

    public void print(NumberFormat format, int width) {
        print(new PrintWriter(System.out, true), format, width);
    }

    // DecimalFormat is a little disappointing coming from Fortran or C's printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths.  Consequently, we'll pass the desired column width in as an
    // argument and do the extra padding ourselves.

    /**
     * Print the matrix to the output stream.  Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
     *
     * @param output the output stream.
     * @param format A formatting object to format the matrix elements
     * @param width  Column width.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */

    public void print(PrintWriter output, NumberFormat format, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(data.get(j).value(i)); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++)
                    output.print(' ');
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }


}
