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
 * Validates an order item.<br/><br/>
 * Created: 23.03.2008 06:46:41
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class OrderItemValidator extends EntityValidator {

    private EANValidator eanValidator = new EANValidator();

    public OrderItemValidator() {
        this("order_item");
    }

    public OrderItemValidator(String entityName) {
        super(entityName);
    }

    @Override
    public boolean valid(Entity item) {
        if (!super.valid(item))
            return false;
        if ((Number) item.getComponent("id") == null)
            return false;
        if ((Number) item.getComponent("order_id") == null)
            return false;
        Integer n = (Integer) item.getComponent("number_of_items");
        if (n == null || n <= 0)
            return false;
        if (!eanValidator.isValid((String) item.getComponent("product_ean_code"), null))
            return false;
        Number totalPrice = (Number) item.getComponent("total_price");
        if (totalPrice == null || totalPrice.doubleValue() < 0)
            return false;
        return true;
    }
}
