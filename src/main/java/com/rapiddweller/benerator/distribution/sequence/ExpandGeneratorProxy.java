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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.engine.BeneratorOpts;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link GeneratorProxy} implementation that supports distribution of unlimited data volumes
 * (provided by a source generator) in a unique or non-unique manner.
 * Handling of unlimited data volume is provided with a cache of limited size,
 * distribution is implemented by distributing source data randomly over buckets
 * and uniqueness can be assured (when setting duplicationQuota to 0) by removing
 * used data from its bucket.<br/>
 * For maximum efficiency, source data first is randomly distributed over buckets of limited size.
 * As long as further source data is available, the proxy randomly selects an entry from a random
 * bucket returns it to the caller and replaces it with new data if no duplicates are allowed or
 * should not be applied in this case.
 * If no more source data is available, the proxy returns the last element from a bucket, allowing
 * to reduce the used bucket size without shifting elements. If a bucket is empty, it is removed.<br/>
 * The buckets were introduced for quickly freeing RAM after usage and for allowing a more erratic
 * behavior in the phase when no more source data is available and elements are taken from bucket end
 * to beginning.<br/><br/>
 * Created: 10.12.2009 11:32:35
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ExpandGeneratorProxy<E> extends GeneratorProxy<E> {

  public static final int MIN_BUCKET_SIZE = 10;
  public static final float DEFAULT_DUPLICATION_QUOTA = 0;

  private float duplicationQuota;
  private int cacheSize;
  private int bucketSize;
  private List<ValueBucket<E>> buckets;
  private RandomProvider random;

  // construction ----------------------------------------------------------------------------------------------------

  public ExpandGeneratorProxy(Generator<E> source, float duplicationQuota) {
    this(source, duplicationQuota, BeneratorOpts.getCacheSize());
  }

  public ExpandGeneratorProxy(Generator<E> source, float duplicationQuota, int cacheSize) {
    this(source, duplicationQuota, BeneratorOpts.getCacheSize(), defaultBucketSize(cacheSize));
  }

  public ExpandGeneratorProxy(Generator<E> source, float duplicationQuota, int cacheSize, int bucketSize) {
    super(source);
    this.duplicationQuota = duplicationQuota;
    this.cacheSize = cacheSize;
    this.bucketSize = bucketSize;
    this.random = BeneratorFactory.getInstance().getRandomProvider();
  }

  public static <T> ExpandGeneratorProxy<T> uniqueProxy(Generator<T> source, int cacheSize, int bucketSize) {
    return new ExpandGeneratorProxy<>(source, 0, cacheSize, bucketSize);
  }

  public static int defaultBucketSize(int cacheSize) {
    return Math.max((int) Math.sqrt(cacheSize), MIN_BUCKET_SIZE);
  }

  // properties ------------------------------------------------------------------------------------------------------

  public int getCacheSize() {
    return cacheSize;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public float getDuplicationQuota() {
    return duplicationQuota;
  }

  public void setDuplicationQuota(float duplicationQuota) {
    this.duplicationQuota = duplicationQuota;
  }

  public int getBucketSize() {
    return bucketSize;
  }

  public void setBucketSize(int bucketSize) {
    this.bucketSize = bucketSize;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    super.init(context);
    createBuckets();
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    if (buckets.isEmpty()) {
      return null;
    }
    int bucketIndex = random.randomIndex(buckets);
    ValueBucket<E> bucket = buckets.get(bucketIndex);
    if (duplicationQuota > 0 && random.randomProbability() < duplicationQuota) {
      return wrapper.wrap(bucket.getRandomElement());
    } else {
      ProductWrapper<E> feed = super.generate(wrapper);
      E result;
      if (feed != null) {
        result = bucket.getAndReplaceRandomElement(feed.unwrap());
      } else {
        result = bucket.getAndRemoveRandomElement();
        if (bucket.isEmpty()) {
          buckets.remove(bucketIndex);
        }
      }
      return wrapper.wrap(result);
    }
  }

  @Override
  public synchronized void reset() {
    super.reset();
    createBuckets();
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  public void printState() {
    for (ValueBucket<E> bucket : buckets) {
      System.out.println(bucket);
    }
  }

  private void createBuckets() {
    int bucketCount = (cacheSize + bucketSize - 1) / bucketSize;
    ArrayList<ValueBucket<E>> infantry = new ArrayList<>(bucketCount);
    buckets = new ArrayList<>(bucketCount);
    for (int i = 0; i < bucketCount; i++) {
      infantry.add(new ValueBucket<>(bucketSize));
    }
    ProductWrapper<E> wrapper;
    for (int i = 0; i < cacheSize && (wrapper = generateFromSource()) != null; i++) {
      int bucketIndex = random.randomIndex(infantry);
      ValueBucket<E> bucket = infantry.get(bucketIndex);
      E feed = wrapper.unwrap();
      bucket.add(feed);
      if (bucket.size() == bucketSize) {
        infantry.remove(bucketIndex);
        buckets.add(bucket);
      }
    }
    for (ValueBucket<E> bucket : infantry) {
      if (bucket.size() > 0) {
        buckets.add(bucket);
      }
    }
  }

}