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

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.common.NumberUtil;

/**
 * Creates UUIDs evaluating IP address, a JVM ID and timestamp.
 * The algorithm is intended to be compatible to the uuid format used by Hibernate, 
 * so it differs from the one defined in <a href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>.<br/>
 * <br/>
 * Created: 15.11.2007 10:52:55
 * @author Volker Bergmann
 */
public class HibUUIDGenerator extends ThreadSafeNonNullGenerator<String> {
    
    private static final String IP_ADDRESS;
    private static final String JVM_ID = NumberUtil.formatHex((int) (System.currentTimeMillis() >>> 8), 8);

    private static final AtomicInteger counter = new AtomicInteger();

    private final String ipJvm;
    private final String separator;

    // construction ----------------------------------------------------------------------------------------------------

    static {
        int ipadd;
        try {
            ipadd = NumberUtil.toInt( InetAddress.getLocalHost().getAddress() );
        } catch (Exception e) {
            ipadd = 0;
        }
        IP_ADDRESS = NumberUtil.formatHex(ipadd, 8);
    }

    public HibUUIDGenerator() {
        this("");
    }

    public HibUUIDGenerator(String separator) {
        this.separator = separator;
        this.ipJvm = IP_ADDRESS + separator + JVM_ID + separator;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public String getSeparator() {
        return separator;
    }

    // Generator interface implementation ------------------------------------------------------------------------------

	@Override
	public Class<String> getGeneratedType() {
	    return String.class;
    }

	@Override
	public String generate() {
        long time = System.currentTimeMillis();
        short count = (short) counter.getAndIncrement();
        if (count < 0)
        	count += Short.MAX_VALUE + 1;
        return ipJvm +
                NumberUtil.formatHex((short) (time >>> 32), 4) + separator +
                NumberUtil.formatHex((int) time, 8) + separator +
                NumberUtil.formatHex(count, 4);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[ipAddress=" + IP_ADDRESS + ", jvmId=" + JVM_ID + ']';
    }

}
