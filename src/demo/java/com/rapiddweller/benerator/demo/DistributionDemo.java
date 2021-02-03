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

import static com.rapiddweller.benerator.util.GeneratorUtil.*;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.distribution.sequence.RandomWalkSequence;
import com.rapiddweller.benerator.distribution.sequence.ShuffleSequence;
import com.rapiddweller.benerator.distribution.sequence.StepSequence;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.StochasticGeneratorFactory;
import com.rapiddweller.model.data.Uniqueness;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Demonstrates the built-in Sequences of 'databene generator'.<br/>
 * <br/>
 * Created: 07.09.2006 21:13:33
 *
 * @author Volker Bergmann
 */
public class DistributionDemo {

  /**
   * The number of invocations
   */
  private static final int N = 128;

  /**
   * Instantiates a frame with a DistributionPane for reach built-in Sequence and usage mode.
   *
   * @param args the input arguments
   * @see DistributionPane
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("DistributionDemo");
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new GridLayout(2, 4));
    contentPane.setBackground(Color.WHITE);
    contentPane.add(createDistributionPane("random",
        SequenceManager.RANDOM_SEQUENCE));
    contentPane.add(createDistributionPane("cumulated",
        SequenceManager.CUMULATED_SEQUENCE));
    contentPane.add(createDistributionPane("randomWalk[0,2]",
        new RandomWalkSequence(BigDecimal.valueOf(0),
            BigDecimal.valueOf(2))));
    contentPane.add(createDistributionPane("randomWalk[-1,1]",
        new RandomWalkSequence(BigDecimal.valueOf(-1),
            BigDecimal.valueOf(1))));
    contentPane.add(createDistributionPane("step[1]",
        new StepSequence(BigDecimal.ONE)));
    contentPane.add(createDistributionPane("wedge",
        SequenceManager.WEDGE_SEQUENCE));
    contentPane.add(createDistributionPane("shuffle",
        new ShuffleSequence(BigDecimal.valueOf(8))));
    contentPane.add(createDistributionPane("bitreverse",
        SequenceManager.BIT_REVERSE_SEQUENCE));
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  private static DistributionPane createDistributionPane(String label,
                                                         Sequence sequence) {
    Generator<Integer> generator = new StochasticGeneratorFactory()
        .createNumberGenerator(Integer.class, 0, true, N - 1, true, 1,
            sequence, Uniqueness.NONE);
    generator.init(new DefaultBeneratorContext());
    return new DistributionPane(label, generator);
  }

  /**
   * Pane that displays a title and a visualization of the Sequence's products
   */
  private static class DistributionPane extends Component {

    private static final long serialVersionUID = -437124282866811738L;

    /**
     * The title to display on top of the pane
     */
    private String title;

    /**
     * The number generator to use
     */
    private Generator<Integer> generator;

    /**
     * Initializes the pane's attributes
     *
     * @param title     the title
     * @param generator the generator
     */
    public DistributionPane(String title, Generator<Integer> generator) {
      this.title = title;
      this.generator = generator;
    }

    /**
     * Paint.
     *
     * @param g the g
     * @see Component#paint(java.awt.Graphics) Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
      super.paint(g);
      g.drawString(title, 0, 10);
      for (int i = 0; i < N; i++) {
        Integer y = generateNonNull(generator);
        if (y != null) {
          g.fillRect(i, 16 + N - y, 2, 2);
        }
      }
    }

    /**
     * Returns the invocation count multiplied by the magnification factor (2) in each dimension
     *
     * @return the preferred size
     */
    @Override
    public Dimension getPreferredSize() {
      return new Dimension(N * 2, N * 2);
    }
  }

}
