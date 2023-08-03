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

package com.rapiddweller.domain.family;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a family container that contains all generated familyPerson each time generation<br/><br/>
 * FamilyPerson can be retrieved by FamilyPersonID or role level: parent-grandparent-children<br/><br/>
 *
 */
public class FamilyContainer {
  private final List<FamilyPerson> familyPersonList;
  private final Set<FamilyRole> parentRoles = Set.of(FamilyRole.FATHER, FamilyRole.MOTHER, FamilyRole.FATHER_DIVERSE, FamilyRole.MOTHER_DIVERSE);
  private final Set<FamilyRole> childrenRoles = Set.of(FamilyRole.SON, FamilyRole.DAUGHTER, FamilyRole.TWIN_SON, FamilyRole.TWIN_DAUGHTER);
  private final Set<FamilyRole> grandParentRoles = Set.of(FamilyRole.GRANDFATHER, FamilyRole.GRANDMOTHER);
  // Constructor -----------------------------------------------------------------------------------------------
  public FamilyContainer() {
    this.familyPersonList = new ArrayList<>();
  }

  // Getter/Setter -----------------------------------------------------------------------------------------------
  public List<FamilyPerson> getFamilyPersonList() {
    return familyPersonList;
  }

  // Get FamilyPerson Util -----------------------------------------------------------------------------------------------
  public FamilyPerson getPersonByID(Long personID){
    return familyPersonList.stream()
            .filter(familyPerson -> familyPerson.getPersonID() == personID)
            .findFirst()
            .orElse(null);
  }

  public int getParentCount(){
    return countMemberWithRoleInList(parentRoles);
  }

  public int getChildrenCount(){
    return countMemberWithRoleInList(childrenRoles);
  }

  public int getGrandparentCount(){
    return countMemberWithRoleInList(grandParentRoles);
  }

  public List<FamilyPerson> getParents(){
    return getMemberWithRoleInList(parentRoles);
  }

  public List<FamilyPerson> getChildren(){
    return getMemberWithRoleInList(childrenRoles);
  }

  public List<FamilyPerson> getGrandparents(){
    return getMemberWithRoleInList(grandParentRoles);
  }

  public int getNumberOfRoleInFamily(FamilyRole role) {
    return countMemberWithRoleInList(Set.of(role));
  }

  // Add FamilyPerson Util -----------------------------------------------------------------------------------------------
  public void addFamilyPerson(FamilyPerson member){
    if (member != null) {
      this.familyPersonList.add(member);
    }
  }

  public void addListOfFamilyPerson(List<FamilyPerson> memberList){
    if (memberList != null) {
      this.familyPersonList.addAll(memberList);
    }
  }

  // Private Helper -----------------------------------------------------------------------------------------------
  private int countMemberWithRoleInList(Set<FamilyRole> roles){
    return (int) familyPersonList.stream()
            .filter(familyPerson -> roles.contains(familyPerson.getFamilyRole()))
            .count();
  }

  private List<FamilyPerson> getMemberWithRoleInList(Set<FamilyRole> roles){
    return familyPersonList.stream()
            .filter(familyPerson -> roles.contains(familyPerson.getFamilyRole()))
            .collect(Collectors.toList());
  }
  // Object Override -----------------------------------------------------------------------------------------------
  @Override
  public synchronized String toString() {
    return getClass().getSimpleName();
  }
}
