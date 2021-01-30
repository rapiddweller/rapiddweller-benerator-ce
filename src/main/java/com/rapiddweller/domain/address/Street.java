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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.util.RandomUtil;

import java.util.Random;

/**
 * Represents a street and serves as generator for house numbers.<br/>
 * <br/>
 * Created: 11.06.2006 08:09:21
 */
public class Street {

    private final City city;
    private String name;
    private int maxHouseNumber;
    private final Random random;

    public Street(City city, String name) {
        this(city, name, 50);
    }

    public Street(City city, String name, int maxHouseNumber) {
        this.city = city;
        this.name = name;
        this.maxHouseNumber = maxHouseNumber;
        this.random = new Random();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxHouseNumber() {
        return maxHouseNumber;
    }

    public void setMaxHouseNumber(int maxHouseNumber) {
        this.maxHouseNumber = maxHouseNumber;
    }

    public String[] generateHouseNumberWithPostalCode() {
        return new String[] {
                String.valueOf(random(1, maxHouseNumber)),
                RandomUtil.randomElement(city.getPostalCodes())
        };
    }

    private int random(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
