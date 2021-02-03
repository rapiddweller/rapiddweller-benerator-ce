/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package shop;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import javax.validation.ConstraintValidatorContext;

import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;

/**
 * Validates a price.<br/><br/>
 * Created: 26.03.2008 12:21:01
 *
 * @author Volker Bergmann
 */
public class PriceValidator
    extends AbstractConstraintValidator<Annotation, BigDecimal> {

  private int fractionDigits;

  /**
   * Instantiates a new Price validator.
   */
  public PriceValidator() {
    this(2);
  }

  /**
   * Instantiates a new Price validator.
   *
   * @param fractionDigits the fraction digits
   */
  public PriceValidator(int fractionDigits) {
    this.fractionDigits = fractionDigits;
  }

  /**
   * Is valid boolean.
   *
   * @param price   the price
   * @param context the context
   * @return the boolean
   */
  public boolean isValid(BigDecimal price,
                         ConstraintValidatorContext context) {
    return (price.scale() <= fractionDigits);
  }
}
