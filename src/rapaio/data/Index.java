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

package rapaio.data;

import java.util.Arrays;

/**
 * Builds a numeric variable which stores values as 32-bit integers.
 * There are two general usage scenarios: use variable as an
 * positive integer index and save storage for numeric
 * variables from Z loosing decimal precision.
 * <p>
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public final class Index extends AbstractVar {

    private static final int MISSING_VALUE = Integer.MIN_VALUE;
    private int[] data;
    private int rows;

    // static builders

    /**
     * Builds an empty index var of size 0
     *
     * @return new instance of index var
     */
    public static Index newEmpty() {
        return new Index(0, 0, 0);
    }

    /**
     * Builds an index of given size filled with missing values
     *
     * @param rows index size
     * @return new instance of index var
     */
    public static Index newEmpty(int rows) {
        return new Index(rows, rows, 0);
    }

    /**
     * Builds an index of size 1 filled with the given value
     *
     * @param value fill value
     * @return new instance of index var
     */
    public static Index newScalar(int value) {
        return new Index(1, 1, value);
    }

    /**
     * Builds an index var of given size with given fill value
     *
     * @param rows  index size
     * @param value fill value
     * @return new instance of index var
     */
    public static Index newFill(int rows, int value) {
        return new Index(rows, rows, value);
    }

    /**
     * Builds an index with values copied from a given array
     *
     * @param values given array of values
     * @return new instance of index var
     */
    public static Index newCopyOf(int... values) {
        Index index = new Index(0, 0, 0);
        index.data = Arrays.copyOf(values, values.length);
        index.rows = values.length;
        return index;
    }

    /**
     * Builds an index as a wrapper over a given array of index values
     *
     * @param values given array of values
     * @return new instance of index var
     */
    public static Index newWrapOf(int... values) {
        Index index = new Index(0, 0, 0);
        index.data = values;
        index.rows = values.length;
        return index;
    }

    /**
     * Builds an index of given size as a ascending sequence starting with 0
     *
     * @param len size of the index
     * @return new instance of index var
     */
    public static Index newSeq(int len) {
        return newSeq(0, len, 1);
    }

    /**
     * Builds an index of given size as ascending sequence with a given start value
     *
     * @param start start value
     * @param len   size of the index
     * @return new instance of index var
     */
    public static Index newSeq(int start, int len) {
        return newSeq(start, len, 1);
    }

    /**
     * Builds an index of given size as ascending sequence with a given start value and a given step
     *
     * @param start start value
     * @param len   size of the index
     * @param step  increment value
     * @return new instance of index var
     */
    public static Index newSeq(final int start, final int len, final int step) {
        Index index = new Index(len, len, 0);
        int s = start;
        for (int i = 0; i < len; i++) {
            index.data[i] = s;
            s = s + step;
        }
        return index;
    }

    // private constructor, only public static builders available

    private Index(int rows, int capacity, int fill) {
        if (rows < 0) {
            throw new IllegalArgumentException("Illegal row count: " + rows);
        }
        this.data = new int[capacity];
        this.rows = rows;
        if (fill != 0)
            Arrays.fill(data, 0, rows, fill);
    }

    @Override
    public Index withName(String name) {
        return (Index) super.withName(name);
    }


    private void ensureCapacityInternal(int minCapacity) {
        // overflow-conscious code
        if (minCapacity < data.length)
            return;
        int oldCapacity = data.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // minCapacity is usually close to size, so this is a win:
        data = Arrays.copyOf(data, newCapacity);
    }

    @Override
    public VarType type() {
        return VarType.INDEX;
    }

    @Override
    public int rowCount() {
        return rows;
    }

    @Override
    public Var bindRows(Var var) {
        return BoundVar.newFrom(this, var);
    }

    @Override
    public Var mapRows(Mapping mapping) {
        return MappedVar.newByRows(this, mapping);
    }

    @Override
    public int index(int row) {
        return data[row];
    }

    @Override
    public void setIndex(int row, int value) {
        data[row] = value;
    }

    @Override
    public void addIndex(int value) {
        ensureCapacityInternal(rows + 1);
        data[rows] = value;
        rows++;
    }

    @Override
    public double value(int row) {
        return index(row);
    }

    @Override
    public void setValue(int row, double value) {
        setIndex(row, (int) Math.rint(value));
    }

    @Override
    public void addValue(double value) {
        addIndex((int) Math.rint(value));
    }

    @Override
    public String label(int row) {
        if (missing(row))
            return "?";
        return String.valueOf(index(row));
    }

    @Override
    public void setLabel(int row, String value) {
        if ("?".equals(value)) {
            setMissing(row);
            return;
        }
        setIndex(row, Integer.parseInt(value));
    }

    @Override
    public void addLabel(String value) {
        if ("?".equals(value)) {
            addMissing();
            return;
        }
        addIndex(Integer.parseInt(value));
    }

    @Override
    public String[] dictionary() {
        throw new IllegalArgumentException("Operation not available for index vectors.");
    }

    @Override
    public void setDictionary(String[] dict) {
        throw new IllegalArgumentException("Operation not available for index vectors.");
    }

    @Override
    public boolean binary(int row) {
        return index(row) == 1;
    }

    @Override
    public void setBinary(int row, boolean value) {
        setIndex(row, value ? 1 : 0);
    }

    @Override
    public void addBinary(boolean value) {
        addIndex(value ? 1 : 0);
    }

    @Override
    public long stamp(int row) {
        return index(row);
    }

    @Override
    public void setStamp(int row, long value) {
        setIndex(row, Integer.valueOf(String.valueOf(value)));
    }

    @Override
    public void addStamp(long value) {
        addIndex(Integer.valueOf(String.valueOf(value)));
    }

    @Override
    public boolean missing(int row) {
        return index(row) == MISSING_VALUE;
    }

    @Override
    public void setMissing(int row) {
        setIndex(row, MISSING_VALUE);
    }

    @Override
    public void addMissing() {
        addIndex(MISSING_VALUE);
    }

    @Override
    public void remove(int index) {
        if (index > rows || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + rows);
        int numMoved = rows - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
            rows--;
        }
    }

    @Override
    public void clear() {
        rows = 0;
    }

    @Override
    public Index solidCopy() {
        Index copy = new Index(rowCount(), rowCount(), 0).withName(name());
        for (int i = 0; i < rowCount(); i++) {
            copy.setIndex(i, index(i));
        }
        return copy;
    }

    @Override
    public String toString() {
        return "Index[" + rowCount() + "]";
    }
}
