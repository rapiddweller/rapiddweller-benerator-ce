/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import com.rapiddweller.domain.product.EANValidator;
import com.rapiddweller.model.data.Entity;

/**
 * Validates a product.<br/><br/>
 * Created: 23.03.2008 06:45:21
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class ProductValidator extends EntityValidator {

    private EANValidator eanValidator = new EANValidator();

    public ProductValidator() {
        this("product");
    }

    public ProductValidator(String entityName) {
        super(entityName);
    }

    @Override
    public boolean valid(Entity product) {
        if (!super.valid(product))
            return false;
        String ean = (String) product.getComponent("ean_code");
        if (!eanValidator.isValid(ean, null))
            return false;
        String name = (String) product.getComponent("name");
        if (name == null || name.length() == 0)
            return false;
        if (product.getComponent("name") == null)
            return false;
        Number price = (Number) product.getComponent("price");
        if (price == null || price.doubleValue() < 0)
            return false;
        if (product.getComponent("manufacturer") == null)
            return false;
        return true;
    }
}
