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

package rapaio.core.sample;

import rapaio.core.RandomSource;
import rapaio.data.Frame;
import rapaio.data.mapping.MappedFrame;
import rapaio.data.mapping.Mapping;
import rapaio.data.stream.FIterator;

import java.util.ArrayList;
import java.util.List;

import static rapaio.data.filters.BaseFilters.shuffle;

/**
 * User: <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
@Deprecated
public class StatSampling {

    public static List<Frame> randomSample(Frame frame, int[] rowCounts) {
        int total = 0;
        for (int rowCount : rowCounts) {
            total += rowCount;
        }
        if (total > frame.rowCount()) {
            throw new IllegalArgumentException("total counts greater than available number of getRowCount");
        }
        List<Frame> result = new ArrayList<>();
        Frame shuffle = shuffle(frame);

        FIterator it = shuffle.stream().iterator();
        for (int i = 0; i < rowCounts.length; i++) {
            for (int j = 0; j < rowCounts[i]; j++) {
                it.next();
                it.collect(String.valueOf(i));
            }
            result.add(it.mappedFrame(String.valueOf(i)));
        }
        while (it.hasNext()) {
            it.next();
            it.collect(String.valueOf(rowCounts.length));
        }
        Frame last = it.mappedFrame(String.valueOf(rowCounts.length));
        if (last != null) {
            result.add(last);
        }
        return result;
    }

    public static Frame randomBootstrap(Frame frame) {
        return randomBootstrap(frame, frame.rowCount());
    }

    public static Frame randomBootstrap(Frame frame, int size) {
        Mapping mapping = Mapping.newEmpty();
        for (int i = 0; i < size; i++) {
            int next = RandomSource.nextInt(frame.rowCount());
            mapping.add(next);
        }
        return MappedFrame.newByRow(frame, mapping);
    }
}
