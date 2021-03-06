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

package rapaio.datasets;

import rapaio.data.Frame;

/**
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public abstract class CTask {

    protected String name;
    protected Frame full;
    protected Frame train;
    protected Frame test;
    protected String targetName;

    public String getName() {
        return name;
    }

    public Frame getFullFrame() {
        return full;
    }

    public Frame getTrainFrame() {
        return train;
    }

    public Frame getTestFrame() {
        return test;
    }

    public String getTargetName() {
        return targetName;
    }

    public abstract boolean reSample(double p, boolean replacement);
}
