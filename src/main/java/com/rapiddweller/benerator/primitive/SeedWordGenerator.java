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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.sample.SeedGenerator;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.domain.lang.Noun;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Generates words based on a word seed.<br/><br/>
 * Created at 11.07.2009 19:30:12
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SeedWordGenerator extends NonNullGeneratorWrapper<Character[], String> {

  private static final int DEFAULT_DEPTH = 4;

  public SeedWordGenerator() {
    this(null, DEFAULT_DEPTH);
  }

  public SeedWordGenerator(Iterator<String> seed, int depth) {
    super(createSource(seed, depth));
  }

  private static SeedGenerator<Character> createSource(Iterator<String> seed, int depth) {
    if (seed == null) {
      seed = defaultNounIterator();
    }
    SeedGenerator<Character> result = new SeedGenerator<>(Character.class, depth);
    while (seed.hasNext()) {
      char[] charArray = seed.next().toCharArray();
      Character[] objectSample = new Character[charArray.length];
      for (int i = 0; i < charArray.length; i++) {
        objectSample[i] = charArray[i];
      }
      result.addSample(objectSample);
    }
    return result;
  }


  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public String generate() {
    assertInitialized();
    return toString(generateFromNotNullSource());
  }

  private static String toString(Character[] chars) {
    StringBuilder builder = new StringBuilder(chars.length);
    for (char c : chars) {
      builder.append(c);
    }
    return builder.toString();
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private static Iterator<String> defaultNounIterator() {
    try {
      Iterator<String> iterator = getNounIterator(Locale.getDefault());
      return (iterator != null ? iterator : getNounIterator(LocaleUtil.getFallbackLocale()));
    } catch (Exception e) {
      throw new ConfigurationError("Error loading noun definition", e);
    }
  }

  private static Iterator<String> getNounIterator(Locale locale) throws IOException {
    Collection<Noun> nouns = Noun.getInstances(locale);
    Set<String> words = new HashSet<>(nouns.size() * 2);
    for (Noun noun : nouns) {
      if (noun.getSingular() != null) {
        words.add(noun.getSingular());
      }
      if (noun.getPlural() != null) {
        words.add(noun.getPlural());
      }
    }
    return words.iterator();
  }

  public void printState() {
    System.out.println(getClass().getSimpleName());
    ((SeedGenerator<Character>) getSource()).printState("  ");
  }

}
