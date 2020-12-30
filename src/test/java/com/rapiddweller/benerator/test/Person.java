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

package com.rapiddweller.benerator.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rapiddweller.common.NullSafeComparator;

/**
 * JavaBean class for testing.<br/>
 * <br/>
 * Created at 28.12.2008 11:34:47
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class Person {
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public String name;
	public Date birthDate;
	public int score;
	public boolean registered;
	public char rank;
	
	// constructors ----------------------------------------------------------------------------------------------------
	
	public Person() {
		this("anonymous", null, 0, false, 'C');
	}

	public Person(String name, Date birthDate, int score, boolean registered, char rank) {
		this.name = name;
		this.birthDate = birthDate;
		this.score = score;
		this.registered = registered;
		this.rank = rank;
	}
	
	// properties ------------------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public int getScore() {
		return score;
	}
	
	public void setScore(int age) {
		this.score = age;
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	
	public char getRank() {
		return rank;
	}
	
	public void setRank(char rank) {
		this.rank = rank;
	}
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rank;
		result = prime * result + (registered ? 1231 : 1237);
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person that = (Person) obj;
		return NullSafeComparator.equals(this.birthDate, that.birthDate) 
			&& NullSafeComparator.equals(this.name, that.name)
			&& this.rank == that.rank
			&& this.registered == that.registered
			&& this.score == that.score;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name='" + name + "', " +
				"birthDate=" + (birthDate != null ? df.format(birthDate) : "null") + ", " +
				"score=" + score + ", registered=" + registered + ", rank='" + rank + "'";
	}
	
}
