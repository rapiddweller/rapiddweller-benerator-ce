/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.demo;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.AbstractWeightFunction;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.model.data.Uniqueness;

import javax.swing.*;
import java.awt.*;

/**
 * Demonstrates the use of Sequences and weight functions for generating numbers.
 * For the horizontal distribution, a weight function of sin^2(cx) is used, vertically
 * a Sequence of type 'cumulated'.
 * <br/>
 * Created: 07.09.2006 19:06:16
 *
 * @see XFunction<br/>
 */
public class ScatterplotDemo extends Component {

  private static final long serialVersionUID = 5264230937667632984L;

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("ScatterplotDemo");
    frame.getContentPane().add(new ScatterplotDemo(), BorderLayout.CENTER);
    frame.getContentPane().setBackground(Color.WHITE);
    frame.setBounds(0, 0, (int) (Math.PI * 150), 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * Paint.
   *
   * @param g the g
   */
  @Override
  public void paint(Graphics g) {
    BeneratorContext context = new DefaultBeneratorContext();
    GeneratorFactory generatorFactory = context.getGeneratorFactory();
    NonNullGenerator<Integer> xGen = generatorFactory
        .createNumberGenerator(Integer.class, 0, true, getWidth(), true,
            1, new XFunction(), Uniqueness.NONE);
    xGen.init(context);
    NonNullGenerator<Integer> yGen = generatorFactory
        .createNumberGenerator(Integer.class, 0, true, getHeight(),
            true, 1, SequenceManager.CUMULATED_SEQUENCE,
            Uniqueness.NONE);
    yGen.init(context);
    int n = getWidth() * getHeight() / 16;
    for (int i = 0; i < n; i++) {
      int x = xGen.generate();
      int y = yGen.generate();
      g.drawLine(x, y, x, y);
    }
  }

  /**
   * The type X function.
   */
  static class XFunction extends AbstractWeightFunction {
    /**
     * Value double.
     *
     * @param param the param
     * @return the double
     */
    public double value(double param) {
      double s = Math.sin(param / 30);
      return s * s;
    }
  }
}
