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

package com.rapiddweller.benerator.util;

import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a text file, shuffles its lines and writes it to another file.<br/>
 * <br/>
 * Created: 16.07.2007 20:29:10
 */
public class LineShuffler {

  /**
   * The constant logger.
   */
  public static final Logger logger = LogManager.getLogger(LineShuffler.class);

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      printHelp();
      System.exit(-1);
    }
    String inFilename = args[0];
    String outFilename = args[1];
    int bufferSize = (args.length > 2 ? Integer.parseInt(args[2]) : 100000);
    shuffle(inFilename, outFilename, bufferSize);
  }

  /**
   * Shuffle.
   *
   * @param inFilename  the in filename
   * @param outFilename the out filename
   * @param bufferSize  the buffer size
   * @throws IOException the io exception
   */
  public static void shuffle(String inFilename, String outFilename, int bufferSize) throws IOException {
    logger.info("shuffling " + inFilename + " and writing to " + outFilename + " (max. " + bufferSize + " lines)");
    ReaderLineIterator iterator = new ReaderLineIterator(new BufferedReader(IOUtil.getReaderForURI(inFilename)));
    List<String> lines = read(bufferSize, iterator);
    shuffle(lines);
    save(lines, outFilename);
  }

  /**
   * Shuffle.
   *
   * @param lines the lines
   */
  public static void shuffle(List<String> lines) {
    int size = lines.size();
    //Generator<Integer> indexGenerator = new IntegerGenerator(0, size - 1, 1, Sequence.RANDOM);
    int iterations = size / 2;
    for (int i = 0; i < iterations; i++) {
      int i1 = RandomUtil.randomInt(0, size - 1);
      int i2;
      do {
        i2 = RandomUtil.randomInt(0, size - 1);
      } while (i1 == i2);
      String tmp = lines.get(i1);
      lines.set(i1, lines.get(i2));
      lines.set(i2, tmp);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private static List<String> read(int bufferSize, ReaderLineIterator iterator) {
    List<String> lines = new ArrayList<>(Math.max(100000, bufferSize));
    int lineCount = 0;
    while (iterator.hasNext() && lineCount < bufferSize) {
      String line = iterator.next();
      if (!StringUtil.isEmpty(line)) {
        lines.add(line);
        lineCount++;
        if (lineCount % 100000 == 99999) {
          logger.info("parsed " + lineCount + " lines");
        }
      }
    }
    return lines;
  }

  private static void save(List<String> lines, String outputFilename) throws IOException {
    logger.info("saving " + outputFilename + "...");
    PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
    try {
      for (String line : lines) {
        printer.println(line);
      }
    } finally {
      IOUtil.close(printer);
    }
  }

  private static void printHelp() {
    ConsoleInfoPrinter.printHelp("Parameters: inFile outFile [buffer size]");
  }
}
