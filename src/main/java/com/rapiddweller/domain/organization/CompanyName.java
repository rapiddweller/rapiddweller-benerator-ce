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

package com.rapiddweller.domain.organization;

/**
 * Assembles the parts of a company name, providing access to full name, short name and name parts
 * like core name (which is the shortName), sector, location and legal form.<br/><br/>
 * Created: 10.10.2010 17:28:01
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class CompanyName {

  private String shortName;
  private String sector;
  private String location;
  private String legalForm;

  private String datasetName;

  /**
   * Gets short name.
   *
   * @return the short name
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Sets short name.
   *
   * @param shortName the short name
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  /**
   * Gets sector.
   *
   * @return the sector
   */
  public String getSector() {
    return sector;
  }

  /**
   * Sets sector.
   *
   * @param sector the sector
   */
  public void setSector(String sector) {
    this.sector = sector;
  }

  /**
   * Gets location.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets location.
   *
   * @param location the location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Gets legal form.
   *
   * @return the legal form
   */
  public String getLegalForm() {
    return legalForm;
  }

  /**
   * Sets legal form.
   *
   * @param legalForm the legal form
   */
  public void setLegalForm(String legalForm) {
    this.legalForm = legalForm;
  }

  /**
   * Gets dataset name.
   *
   * @return the dataset name
   */
  public String getDatasetName() {
    return datasetName;
  }

  /**
   * Sets dataset name.
   *
   * @param datasetName the dataset name
   */
  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getFullName() {
    StringBuilder builder = new StringBuilder(shortName);
    if (sector != null) {
      builder.append(' ').append(sector);
    }
    if (location != null) {
      builder.append(' ').append(location);
    }
    if (location != null) {
      builder.append(' ').append(legalForm);
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return getFullName();
  }

}
