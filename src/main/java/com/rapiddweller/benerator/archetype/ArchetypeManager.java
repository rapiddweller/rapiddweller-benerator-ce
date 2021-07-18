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

package com.rapiddweller.benerator.archetype;

import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ReaderLineIterator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Looks up and manages Benerator archetypes.<br/><br/>
 * Created at 18.02.2009 07:35:52
 *
 * @author Volker Bergmann
 * @since 0.5.9
 */
public class ArchetypeManager {

  /**
   * The Archetype folder.
   */
  static final String ARCHETYPE_FOLDER = "/com/rapiddweller/benerator/archetype";
  /**
   * The Archetypes index.
   */
  static final String ARCHETYPES_INDEX = ARCHETYPE_FOLDER + "/archetypes.txt";
  /**
   * The Archetypes index url.
   */
  static final String ARCHETYPES_INDEX_URL = Objects.requireNonNull(ArchetypeManager.class.getResource(ARCHETYPES_INDEX)).toString();
  /**
   * The Archetype folder url.
   */
  static final URL ARCHETYPE_FOLDER_URL;

  static {
    try {
      ARCHETYPE_FOLDER_URL = new URL(ARCHETYPES_INDEX_URL.substring(0, ARCHETYPES_INDEX_URL.lastIndexOf('/')));
    } catch (MalformedURLException e) {
      throw new ConfigurationError(e);
    }
  }

  private final Archetype[] archetypes;

  private ArchetypeManager() {
    try {
      // read archetypes in the order specified in the file 'archetypes.txt'
      ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(ARCHETYPES_INDEX));
      ArrayBuilder<Archetype> builder = new ArrayBuilder<>(Archetype.class);
      while (iterator.hasNext()) {
        String name = iterator.next();
        URL archUrl = new URL(ARCHETYPE_FOLDER_URL.toString() + "/" + name);
        Archetype archetype = new Archetype(archUrl);
        builder.add(archetype);
      }
      this.archetypes = builder.toArray();
    } catch (IOException e) {
      throw new ConfigurationError("Error parsing archetype definitions", e);
    }
  }

  /**
   * Get archetypes archetype [ ].
   *
   * @return the archetype [ ]
   */
  public Archetype[] getArchetypes() {
    return archetypes;
  }

  private static ArchetypeManager instance;

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static ArchetypeManager getInstance() {
    if (instance == null) {
      instance = new ArchetypeManager();
    }
    return instance;
  }

  /**
   * Gets default archetype.
   *
   * @return the default archetype
   */
  public Archetype getDefaultArchetype() {
    for (Archetype candidate : archetypes) {
      if ("simple".equals(candidate.getId())) {
        return candidate;
      }
    }
    return archetypes[0];
  }

}
