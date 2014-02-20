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

package rapaio.server;

import java.io.Serializable;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class CommandBytes implements Serializable {

    public static enum Type {

        REMOTE,
        PRINT,
        DRAW,
        CONFIG
    }

    private final Type type;
    private final String name;
    private final String value;
    private final byte[] bytes;
    private int textWidth;
    private int graphicalWidth;
    private int graphicalHeight;

    private CommandBytes(Type type, String name, String value, byte[] bytes) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.bytes = bytes;
    }

    public static CommandBytes newRemote(String name, byte[] bytes) {
        return new CommandBytes(Type.REMOTE, name, null, bytes);
    }

    public static CommandBytes newPrint(String name, String value) {
        return new CommandBytes(Type.PRINT, name, value, null);
    }

    public static CommandBytes newDraw(byte[] bytes) {
        return new CommandBytes(Type.DRAW, null, null, bytes);
    }

    public static CommandBytes newConfig() {
        return new CommandBytes(Type.CONFIG, null, null, null);
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public int getGraphicalWidth() {
        return graphicalWidth;
    }

    public void setGraphicalWidth(int graphicalWidth) {
        this.graphicalWidth = graphicalWidth;
    }

    public int getGraphicalHeight() {
        return graphicalHeight;
    }

    public void setGraphicalHeight(int graphicalHeight) {
        this.graphicalHeight = graphicalHeight;
    }

}