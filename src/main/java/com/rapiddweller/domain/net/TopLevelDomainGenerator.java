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

package com.rapiddweller.domain.net;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.sample.WeightedCSVSampleGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.Encodings;

/**
 * A TopLevelDomainGenerator.<br/><br/>
 * Created at 20.04.2008 08:01:55
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public class TopLevelDomainGenerator extends WeightedCSVSampleGenerator<String>
    implements NonNullGenerator<String> {

  /**
   * Instantiates a new Top level domain generator.
   */
  public TopLevelDomainGenerator() {
    super("/com/rapiddweller/domain/net/tld.csv", Encodings.UTF_8, ',');
  }

  @Override
  public String generate() {
    return GeneratorUtil.generateNonNull(this);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
