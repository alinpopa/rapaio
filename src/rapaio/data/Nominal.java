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

import java.util.*;

/**
 * Nominal var contains values for categorical observations where order of labels is not important.
 * <p>
 * The domain of the definition is called dictionary and is given at construction time or can be changed latter.
 * <p>
 * This type of variable accepts two value representation: as labels and as indexes.
 * <p>
 * Label representation is the natural representation since in experiments
 * the nominal vectors are given as string values.
 * <p>
 * The index representation is learn based on the term dictionary and is used often for performance
 * reasons instead of label representation, where the actual label value does not matter.
 * <p>
 * Even if index values is an integer number the order of the indexes for
 * nominal variables is irrelevant.
 *
 * @author Aurelian Tutuianu
 */
public final class Nominal extends FactorBase {

    /**
     * Builds a new empty nominal variable
     *
     * @return new variable instance of nominal type
     */
    public static Nominal newEmpty() {
        return new Nominal();
    }

    /**
     * Builds a new nominal variable of given size, with given term dictionary, filled with missing values.
     *
     * @param rows variable size
     * @param dict term dictionary
     * @return new variable instance of nominal type
     */
    public static Nominal newEmpty(int rows, String... dict) {
        return Nominal.newEmpty(rows, Arrays.asList(dict));
    }

    /**
     * Builds a new nominal variable of given size, with given term dictionary, filled with missing values.
     *
     * @param rows variable size
     * @param dict term dictionary
     * @return new variable instance of nominal type
     */
    public static Nominal newEmpty(int rows, Collection<String> dict) {
        Nominal nominal = new Nominal();
        HashSet<String> used = new HashSet<>();
        used.add("?");
        for (String next : dict) {
            if (used.contains(next)) continue;
            used.add(next);
            nominal.dict.add(next);
            nominal.reverse.put(next, nominal.reverse.size());
        }
        nominal.data = new int[rows];
        nominal.rows = rows;
        return nominal;
    }

    public static Nominal newCopyOf(String... values) {
        Nominal nominal = Nominal.newEmpty();
        for (String value : values)
            nominal.addLabel(value);
        return nominal;
    }

    private Nominal() {
        // set the missing value
        this.reverse = new HashMap<>();
        this.reverse.put("?", 0);
        this.dict = new ArrayList<>();
        this.dict.add("?");
        data = new int[0];
        rows = 0;
    }

    @Override
    public Nominal withName(String name) {
        return (Nominal) super.withName(name);
    }

    @Override
    public VarType type() {
        return VarType.NOMINAL;
    }

    @Override
    public Nominal solidCopy() {
        Nominal copy = Nominal.newEmpty(rowCount(), dictionary()).withName(name());
        for (int i = 0; i < rowCount(); i++) {
            copy.setLabel(i, label(i));
        }
        return copy;
    }

    @Override
    public String toString() {
        return "Nominal[" + rowCount() + "]";
    }
}
