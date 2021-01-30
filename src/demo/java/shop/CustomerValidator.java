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

import java.util.Date;

import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.Entity;

/**
 * Validates a customer.<br/><br/>
 * Created: 23.03.2008 06:46:33
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class CustomerValidator extends EntityValidator {

    public CustomerValidator() {
        this("customer");
    }

    public CustomerValidator(String entityName) {
        super(entityName);
    }

    @Override
    public boolean valid(Entity customer) {
        if (!super.valid(customer)) {
            return false;
        }
        if ((Number) customer.getComponent("id") == null) {
            return false;
        }
        if (StringUtil.isEmpty((String) customer.getComponent("category"))) {
            return false;
        }
        if (StringUtil.isEmpty((String) customer.getComponent("salutation"))) {
            return false;
        }
        String firstName = (String) customer.getComponent("first_name");
        if (StringUtil.isEmpty(firstName)) {
            return false;
        }
        if (StringUtil.isEmpty((String) customer.getComponent("last_name"))) {
            return false;
        }
        // require date except for Charly Brown
        if ((Date) customer.getComponent("birth_date") == null &&
                !"Charly".equals(firstName)) {
            return false;
        }
        return true;
    }
}
