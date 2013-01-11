/*
 * Ivory: A Hadoop toolkit for web-scale information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ivory.regression.basic;

import ivory.core.eval.Qrels;
import ivory.regression.GroundTruth;
import ivory.regression.GroundTruth.Metric;
import ivory.smrf.retrieval.Accumulator;
import ivory.smrf.retrieval.BatchQueryRunner;

import java.util.Map;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.common.collect.Maps;

import edu.umd.cloud9.collection.DocnoMapping;

public class Web09catB_All {
  private static final Logger LOG = Logger.getLogger(Web09catB_All.class);

  private static String[] ql_base_rawAP = new String[] {
      "1",  "0.3663", "2",  "0.4251", "3",  "0.0007", "4",  "0.0585", "5",  "0.0261",
      "6",  "0.1093", "7",  "0.1250", "8",  "0.0205", "9",  "0.1551", "10", "0.0603",
      "11", "0.4410", "12", "0.1688", "13", "0.0595", "14", "0.0693", "15", "0.3321",
      "16", "0.3222", "17", "0.1071", "18", "0.2644", "19", "0.0000", "20", "0.0000", 
      "21", "0.4158", "22", "0.4482", "23", "0.0060", "24", "0.1606", "25", "0.2705",
      "26", "0.1891", "27", "0.1981", "28", "0.2793", "29", "0.0679", "30", "0.2060",
      "31", "0.4260", "32", "0.0696", "33", "0.4708", "34", "0.0245", "35", "0.4336",
      "36", "0.1028", "37", "0.0500", "38", "0.0874", "39", "0.1265", "40", "0.1879",
      "41", "0.1171", "42", "0.0096", "43", "0.3539", "44", "0.0431", "45", "0.2405",
      "46", "0.7038", "47", "0.4459", "48", "0.1267", "49", "0.2187", "50", "0.0656" }; 

  private static String[] ql_sd_rawAP = new String[] {
      "1",  "0.3603", "2",  "0.4645", "3",  "0.0007", "4",  "0.0585", "5",  "0.0685", 
      "6",  "0.1093", "7",  "0.1383", "8",  "0.0205", "9",  "0.1689", "10", "0.1139",
      "11", "0.4477", "12", "0.1688", "13", "0.0595", "14", "0.0693", "15", "0.3599",
      "16", "0.3457", "17", "0.1200", "18", "0.3395", "19", "0.0000", "20", "0.0000",
      "21", "0.4158", "22", "0.4523", "23", "0.0060", "24", "0.1606", "25", "0.2705",
      "26", "0.2235", "27", "0.1981", "28", "0.2793", "29", "0.0771", "30", "0.2288",
      "31", "0.4260", "32", "0.0605", "33", "0.4704", "34", "0.0265", "35", "0.4336",
      "36", "0.1028", "37", "0.0526", "38", "0.0894", "39", "0.1540", "40", "0.1879",
      "41", "0.1853", "42", "0.0380", "43", "0.3872", "44", "0.0587", "45", "0.2533",
      "46", "0.6951", "47", "0.4580", "48", "0.1167", "49", "0.2513", "50", "0.0664" };

  private static String[] ql_wsd_rawAP = new String[] {
      "1",  "0.5816", "2",  "0.5045", "3",  "0.0007", "4",  "0.0585", "5", "0.1348", 
      "6",  "0.1093", "7",  "0.1410", "8",  "0.0205", "9",  "0.1625", "10", "0.1506",
      "11", "0.4280", "12", "0.1688", "13", "0.0595", "14", "0.0693", "15", "0.3623", 
      "16", "0.3261", "17", "0.1242", "18", "0.3584", "19", "0.0000", "20", "0.0000",
      "21", "0.4158", "22", "0.4572", "23", "0.0060", "24", "0.1606", "25", "0.2705",
      "26", "0.1763", "27", "0.1981", "28", "0.2793", "29", "0.0566", "30", "0.2366",
      "31", "0.4260", "32", "0.0510", "33", "0.4736", "34", "0.0263", "35", "0.4336",
      "36", "0.1028", "37", "0.0631", "38", "0.0881", "39", "0.1592", "40", "0.1879",
      "41", "0.2794", "42", "0.0721", "43", "0.3995", "44", "0.0848", "45", "0.2631",
      "46", "0.6820", "47", "0.3810", "48", "0.1172", "49", "0.2416", "50", "0.0516" }; 

  private static String[] bm25_base_rawAP = new String[] {
      "1",  "0.5212", "2",  "0.5116", "3",  "0.0007", "4",  "0.0597", "5",  "0.0779",
      "6",  "0.1209", "7",  "0.1247", "8",  "0.0242", "9",  "0.1250", "10", "0.0174",
      "11", "0.4282", "12", "0.1549", "13", "0.0600", "14", "0.0806", "15", "0.3438",
      "16", "0.3469", "17", "0.1294", "18", "0.1609", "19", "0.0000", "20", "0.0000",
      "21", "0.4137", "22", "0.4806", "23", "0.0080", "24", "0.1639", "25", "0.2739",
      "26", "0.0527", "27", "0.1993", "28", "0.2890", "29", "0.0119", "30", "0.2322",
      "31", "0.4254", "32", "0.0734", "33", "0.4791", "34", "0.0246", "35", "0.4572",
      "36", "0.1041", "37", "0.0624", "38", "0.1022", "39", "0.1413", "40", "0.1838",
      "41", "0.2528", "42", "0.0180", "43", "0.4975", "44", "0.0502", "45", "0.2768",
      "46", "0.7204", "47", "0.5047", "48", "0.1471", "49", "0.2433", "50", "0.0779" };

  private static String[] bm25_sd_rawAP = new String[] {
      "1",  "0.6232", "2",  "0.4965", "3",  "0.0007", "4",  "0.0597", "5",  "0.2190",
      "6",  "0.1209", "7",  "0.1463", "8",  "0.0242", "9",  "0.1411", "10", "0.1723",
      "11", "0.4080", "12", "0.1548", "13", "0.0600", "14", "0.0806", "15", "0.2993",
      "16", "0.3345", "17", "0.1365", "18", "0.2689", "19", "0.0000", "20", "0.0000",
      "21", "0.4137", "22", "0.4756", "23", "0.0080", "24", "0.1639", "25", "0.2739",
      "26", "0.0993", "27", "0.1993", "28", "0.2890", "29", "0.0330", "30", "0.2495",
      "31", "0.4254", "32", "0.0297", "33", "0.4531", "34", "0.0259", "35", "0.4572",
      "36", "0.1041", "37", "0.0624", "38", "0.1027", "39", "0.1461", "40", "0.1838",
      "41", "0.3555", "42", "0.0958", "43", "0.5270", "44", "0.0813", "45", "0.2791",
      "46", "0.7209", "47", "0.5109", "48", "0.1091", "49", "0.2651", "50", "0.0515" };

  private static String[] bm25_wsd_rawAP = new String[] {
     "1",  "0.6194", "2",  "0.4911", "3",  "0.0007", "4",  "0.0597", "5", "0.2066",
     "6",  "0.1209", "7",  "0.1452", "8",  "0.0242", "9",  "0.1426", "10", "0.1643",
     "11", "0.4062", "12", "0.1549", "13", "0.0600", "14", "0.0806", "15", "0.3171",
     "16", "0.3395", "17", "0.1359", "18", "0.2661", "19", "0.0000", "20", "0.0000",
     "21", "0.4137", "22", "0.4765", "23", "0.0080", "24", "0.1639", "25", "0.2739",
     "26", "0.0883", "27", "0.1993", "28", "0.2890", "29", "0.0348", "30", "0.2527",
     "31", "0.4254", "32", "0.0332", "33", "0.4529", "34", "0.0259", "35", "0.4572",
     "36", "0.1041", "37", "0.0621", "38", "0.1048", "39", "0.1566", "40", "0.1838",
     "41", "0.3460", "42", "0.0887", "43", "0.5381", "44", "0.0809", "45", "0.2790",
     "46", "0.7264", "47", "0.5567", "48", "0.1196", "49", "0.2835", "50", "0.0625" };

  @Test
  public void runRegression() throws Exception {
    String[] params = new String[] {
        "data/clue/run.web09catB.all.xml",
        "data/clue/queries.web09.xml" };

    FileSystem fs = FileSystem.getLocal(new Configuration());

    BatchQueryRunner qr = new BatchQueryRunner(params, fs);

    long start = System.currentTimeMillis();
    qr.runQueries();
    long end = System.currentTimeMillis();

    LOG.info("Total query time: " + (end - start) + "ms");

    verifyAllResults(qr.getModels(), qr.getAllResults(), qr.getDocnoMapping(),
        new Qrels("data/clue/qrels.web09catB.txt"));
  }

  public static void verifyAllResults(Set<String> models,
      Map<String, Map<String, Accumulator[]>> results, DocnoMapping mapping, Qrels qrels) {

    Map<String, GroundTruth> g = Maps.newHashMap();
    g.put("ql-base", new GroundTruth("ql-base", Metric.AP, 50, ql_base_rawAP, 0.1931f));
    g.put("ql-sd", new GroundTruth("ql-sd", Metric.AP, 50, ql_sd_rawAP, 0.2048f));
    g.put("ql-wsd", new GroundTruth("ql-wsd", Metric.AP, 50, ql_wsd_rawAP, 0.2120f));
    g.put("bm25-base", new GroundTruth("bm25-base", Metric.AP, 50, bm25_base_rawAP, 0.2051f));
    g.put("bm25-sd", new GroundTruth("bm25-sd", Metric.AP, 50, bm25_sd_rawAP, 0.2188f));
    g.put("bm25-wsd", new GroundTruth("bm25-wsd", Metric.AP, 50, bm25_wsd_rawAP, 0.2207f));

    for (String model : models) {
      LOG.info("Verifying results of model \"" + model + "\"");

      Map<String, Accumulator[]> r = results.get(model);
      g.get(model).verify(r, mapping, qrels);

      LOG.info("Done!");
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Web09catB_All.class);
  }
}
