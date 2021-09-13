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

package com.rapiddweller.domain.finance;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.primitive.RegexStringGenerator;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Generates {@link BankAccount}s with low validity requirements.<br/><br/>
 * Created at 23.06.2008 11:08:48
 * @author Volker Bergmann
 * @since 0.5.4
 */
public class BankGenerator extends CompositeGenerator<Bank>
    implements NonNullGenerator<Bank> {

  private final Generator<String> bankCodeGenerator;
  private final Generator<String> nameGenerator;
  private final Generator<String> bicGenerator;
  private final Generator<String> binGenerator;
  private WrapperProvider<String> wp;

  public BankGenerator() {
    super(Bank.class);
    this.bankCodeGenerator = BeneratorFactory.getInstance()
        .createVarLengthStringGenerator("[0-9]", 8, 8);
    this.nameGenerator = registerComponent(new RegexStringGenerator(
        "(Deutsche Bank|Dresdner Bank|Commerzbank|Spardabank|HVB)"));
    this.bicGenerator = registerComponent(
        new RegexStringGenerator("[A-Z]{4}DE[A-Z0-9]{2}"));
    this.binGenerator =
        registerComponent(BeneratorFactory.getInstance()
            .createVarLengthStringGenerator("[0-9]", 4, 4));
    wp = new WrapperProvider<>();
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    bankCodeGenerator.init(context);
    nameGenerator.init(context);
    bicGenerator.init(context);
    binGenerator.init(context);
    super.init(context);
  }

  @Override
  public ProductWrapper<Bank> generate(ProductWrapper<Bank> wrapper) {
    return wrapper.wrap(generate());
  }

  @Override
  public Bank generate() {
    String name = generate(nameGenerator);
    String bankCode = generate(bankCodeGenerator);
    String bic = generate(bicGenerator);
    String bin = generate(binGenerator);
    return new Bank(name, bankCode, bic, bin);
  }

  private String generate(Generator<String> generator) {
    ProductWrapper<String> result = generator.generate(wp.get());
    return (result != null ? result.unwrap() : null);
  }

}
