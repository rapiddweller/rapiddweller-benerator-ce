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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.primitive.RandomVarLengthStringGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.domain.address.Country;

/**
 * Generates German {@link BankAccount}s with low validity requirements.<br/><br/>
 * Created at 24.06.2008 08:36:32
 *
 * @author Volker Bergmann
 * @since 0.5.4
 */
public class BankAccountGenerator extends CompositeGenerator<BankAccount>
        implements NonNullGenerator<BankAccount> {

    private final String countryCode;
    private final BankGenerator bankGenerator;
    private final RandomVarLengthStringGenerator accountNumberGenerator;

    public BankAccountGenerator() {
        super(BankAccount.class);
        LocaleUtil.getFallbackLocale();
        this.countryCode = Country.getDefault().getIsoCode();
        this.bankGenerator = registerComponent(new BankGenerator());
        this.accountNumberGenerator = registerComponent(
                new RandomVarLengthStringGenerator("\\d", 10));
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        bankGenerator.init(context);
        accountNumberGenerator.init(context);
        super.init(context);
    }

    @Override
    public ProductWrapper<BankAccount> generate(
            ProductWrapper<BankAccount> wrapper) {
        return wrapper.wrap(generate());
    }

    @Override
    public BankAccount generate() {
        Bank bank = bankGenerator.generate();
        String accountNumber = accountNumberGenerator.generate();
        String iban = createIban(bank, accountNumber);
        return new BankAccount(bank, accountNumber, iban);
    }

    private String createIban(Bank bank, String accountNumber) {
        String builder = countryCode + "00" +
                bank.getBankCode() +
                StringUtil.padLeft(accountNumber, 10, '0');
        return IBANUtil.fixChecksum(builder);
    }

}
