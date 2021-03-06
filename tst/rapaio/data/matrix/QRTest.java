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

import org.junit.Test;
import rapaio.data.Numeric;

import java.text.DecimalFormat;

import static rapaio.data.matrix.MathMatrix.times;

/**
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class QRTest {

	@Test
	public void testQR() {
		Matrix x = new Matrix(new double[][]{
				{1, 1, 1},
				{1, 2, 4},
				{1, 3, 9},
				{1, 4, 16},
				{1, 5, 25}
		});

		Matrix y;
        y = new Matrix(
                Numeric.newCopyOf(2.8, 3.2, 7.1, 6.8, 8.8),
                Numeric.newCopyOf(2.8, 3.2, 7.1, 6.8, 8.9)
        );

        QRDecomposition qr = new QRDecomposition(x);
//        System.out.println("Q");
//        qr.getQ().print(new DecimalFormat("0.000000000"), 14);
//        System.out.println("R");
//        qr.getR().print(new DecimalFormat("0.000000000"), 14);
//        System.out.println("QR");
//        qr.getQR().print(new DecimalFormat("0.000000000"), 14);
//        System.out.println("H");
//        qr.getH().print(new DecimalFormat("0.000000000"), 14);

		qr.solve(y).print(new DecimalFormat("0.000000000"), 14);

		x.print(new DecimalFormat("0.000"), 10);
		times(qr.getQ(), qr.getR()).print(new DecimalFormat("0.000"), 10);
	}
}
