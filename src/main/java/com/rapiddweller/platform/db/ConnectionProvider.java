/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db;

import java.sql.Connection;

/**
 * Common interface for classes that can provide a connection.<br/><br/>
 * Created: 21.02.2022 18:01:51
 * @author Volker Bergmann
 * @since 3.0.0
 */
public interface ConnectionProvider {
	Connection getConnection();
}
