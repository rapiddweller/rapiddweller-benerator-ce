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

import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.Entity;

/**
 * Validates a user.<br/><br/>
 * Created: 23.03.2008 06:45:59
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class UserValidator extends EntityValidator {

  //private Set<String> supportedRoles = CollectionUtil.toSet("administrator", "clerk", "customer");

  /**
   * Instantiates a new User validator.
   *
   * @param entityName the entity name
   */
  public UserValidator(String entityName) {
    super(entityName);
  }

  /**
   * Valid boolean.
   *
   * @param user the user
   * @return the boolean
   */
  @Override
  public boolean valid(Entity user) {
    if (user.getComponent("id") == null) {
      return false;
    }
    if (StringUtil.isEmpty((String) user.getComponent("name"))) {
      return false;
    }
    if (StringUtil.isEmpty((String) user.getComponent("email"))) {
      return false;
    }
    if (StringUtil.isEmpty((String) user.getComponent("password"))) {
      return false;
    }
        /*String roleId = (String) user.getComponent("role_id");
        if (!roles.contains(roleId))
            return false;*/
    Object active = user.getComponent("active");
    if (active instanceof Number) {
      int value = ((Number) active).intValue();
      if (value < 0 || value > 1) {
        return false;
      }
    } else if (active instanceof Boolean) {
      return (Boolean) active;
    } else {
      return false;
    }
    return true;
  }

}
