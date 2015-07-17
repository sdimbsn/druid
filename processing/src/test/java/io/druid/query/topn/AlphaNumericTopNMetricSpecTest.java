/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.druid.query.topn;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Comparator;

public class AlphaNumericTopNMetricSpecTest
{
  // Test derived from sample code listed on Apache 2.0 licensed https://github.com/amjjd/java-alphanum
  @Test
  public void testComparator() throws Exception
  {
    final Comparator<String> comparator = AlphaNumericTopNMetricSpec.comparator;

    // equality
    Assert.assertEquals(0, comparator.compare("", ""));
    Assert.assertEquals(0, comparator.compare("abc", "abc"));
    Assert.assertEquals(0, comparator.compare("123", "123"));
    Assert.assertEquals(0, comparator.compare("abc123", "abc123"));

    // empty strings < non-empty
    Assert.assertTrue(comparator.compare("", "abc") < 0);
    Assert.assertTrue(comparator.compare("abc", "") > 0);

    // numbers < non numeric
    Assert.assertTrue(comparator.compare("123", "abc") < 0);
    Assert.assertTrue(comparator.compare("abc", "123") > 0);

    // numbers ordered numerically
    Assert.assertTrue(comparator.compare("2", "11") < 0);
    Assert.assertTrue(comparator.compare("a2", "a11") < 0);

    // leading zeroes
    Assert.assertTrue(comparator.compare("02", "11") < 0);
    Assert.assertTrue(comparator.compare("02", "002") < 0);

    // decimal points ...
    Assert.assertTrue(comparator.compare("1.3", "1.5") < 0);

    // ... don't work too well
    Assert.assertTrue(comparator.compare("1.3", "1.15") < 0);

  }

  @Test
  public void testSerdeAlphaNumericTopNMetricSpec() throws IOException{
    AlphaNumericTopNMetricSpec expectedMetricSpec = new AlphaNumericTopNMetricSpec(null);
    AlphaNumericTopNMetricSpec expectedMetricSpec1 = new AlphaNumericTopNMetricSpec("test");
    String jsonSpec = "{\n"
                      + "    \"type\": \"alphaNumeric\"\n"
                      + "}";
    String jsonSpec1 = "{\n"
                       + "    \"type\": \"alphaNumeric\",\n"
                       + "    \"previousStop\": \"test\"\n"
                       + "}";
    ObjectMapper jsonMapper = new DefaultObjectMapper();
    TopNMetricSpec actualMetricSpec = jsonMapper.readValue(jsonMapper.writeValueAsString(jsonMapper.readValue(jsonSpec, TopNMetricSpec.class)), AlphaNumericTopNMetricSpec.class);
    TopNMetricSpec actualMetricSpec1 = jsonMapper.readValue(jsonMapper.writeValueAsString(jsonMapper.readValue(jsonSpec1, TopNMetricSpec.class)), AlphaNumericTopNMetricSpec.class);
    Assert.assertEquals(expectedMetricSpec, actualMetricSpec);
    Assert.assertEquals(expectedMetricSpec1, actualMetricSpec1);
  }
}
