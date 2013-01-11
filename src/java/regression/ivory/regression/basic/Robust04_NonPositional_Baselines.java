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

import static ivory.regression.RegressionUtils.loadScoresIntoMap;
import static org.junit.Assert.assertEquals;
import ivory.core.eval.Qrels;
import ivory.core.eval.RankedListEvaluator;
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

public class Robust04_NonPositional_Baselines {
  private static final Logger LOG = Logger.getLogger(Robust04_NonPositional_Baselines.class);

  private static String[] sDirBaseRawAP = new String[] {
      "601", "0.4648", "602", "0.2787", "603", "0.2931", "604", "0.8289", "605", "0.0758",
      "606", "0.4768", "607", "0.2038", "608", "0.0548", "609", "0.3040", "610", "0.0245",
      "611", "0.2730", "612", "0.4695", "613", "0.2278", "614", "0.2014", "615", "0.1071",
      "616", "0.7291", "617", "0.2573", "618", "0.2135", "619", "0.5546", "620", "0.0671",
      "621", "0.3175", "622", "0.0349", "623", "0.3311", "624", "0.2460", "625", "0.0247",
      "626", "0.1542", "627", "0.0140", "628", "0.2397", "629", "0.1319", "630", "0.6110",
      "631", "0.1560", "632", "0.2665", "633", "0.4968", "634", "0.7553", "635", "0.5210",
      "636", "0.1321", "637", "0.4508", "638", "0.0414", "639", "0.1334", "640", "0.3590",
      "641", "0.3169", "642", "0.1531", "643", "0.4792", "644", "0.2338", "645", "0.5992",
      "646", "0.3064", "647", "0.2310", "648", "0.1672", "649", "0.7222", "650", "0.0874",
      "651", "0.0574", "652", "0.3183", "653", "0.5799", "654", "0.4083", "655", "0.0014",
      "656", "0.5132", "657", "0.4083", "658", "0.1280", "659", "0.4606", "660", "0.6591",
      "661", "0.5919", "662", "0.6254", "663", "0.4044", "664", "0.3955", "665", "0.2048",
      "666", "0.0084", "667", "0.3518", "668", "0.3408", "669", "0.1557", "670", "0.1291",
      "671", "0.3049", "672", "0.0000", "673", "0.3175", "674", "0.1371", "675", "0.2941",
      "676", "0.2827", "677", "0.8928", "678", "0.2102", "679", "0.8833", "680", "0.0756",
      "681", "0.3877", "682", "0.2516", "683", "0.0273", "684", "0.0918", "685", "0.2809",
      "686", "0.2515", "687", "0.2149", "688", "0.1168", "689", "0.0060", "690", "0.0047",
      "691", "0.3403", "692", "0.4541", "693", "0.3279", "694", "0.4762", "695", "0.2949",
      "696", "0.2975", "697", "0.1600", "698", "0.4824", "699", "0.4594", "700", "0.4381" };

  private static String[] sDirBaseRawP10 = new String[] {
      "601", "0.3000", "602", "0.3000", "603", "0.2000", "604", "0.6000", "605", "0.1000",
      "606", "0.5000", "607", "0.3000", "608", "0.1000", "609", "0.6000", "610", "0.0000",
      "611", "0.5000", "612", "0.7000", "613", "0.5000", "614", "0.2000", "615", "0.2000",
      "616", "1.0000", "617", "0.5000", "618", "0.3000", "619", "0.7000", "620", "0.1000",
      "621", "0.7000", "622", "0.0000", "623", "0.6000", "624", "0.4000", "625", "0.1000",
      "626", "0.1000", "627", "0.1000", "628", "0.4000", "629", "0.2000", "630", "0.3000",
      "631", "0.2000", "632", "0.6000", "633", "1.0000", "634", "0.8000", "635", "0.8000",
      "636", "0.1000", "637", "0.7000", "638", "0.2000", "639", "0.2000", "640", "0.6000",
      "641", "0.5000", "642", "0.2000", "643", "0.4000", "644", "0.3000", "645", "0.9000",
      "646", "0.4000", "647", "0.6000", "648", "0.5000", "649", "1.0000", "650", "0.2000",
      "651", "0.2000", "652", "0.7000", "653", "0.6000", "654", "1.0000", "655", "0.0000",
      "656", "0.7000", "657", "0.6000", "658", "0.3000", "659", "0.5000", "660", "0.9000",
      "661", "0.8000", "662", "0.8000", "663", "0.5000", "664", "0.4000", "665", "0.4000",
      "666", "0.0000", "667", "0.8000", "668", "0.5000", "669", "0.2000", "670", "0.3000",
      "671", "0.5000", "672", "0.0000", "673", "0.5000", "674", "0.2000", "675", "0.5000",
      "676", "0.3000", "677", "0.8000", "678", "0.3000", "679", "0.6000", "680", "0.2000",
      "681", "0.5000", "682", "0.5000", "683", "0.3000", "684", "0.3000", "685", "0.4000",
      "686", "0.4000", "687", "0.7000", "688", "0.3000", "689", "0.0000", "690", "0.0000",
      "691", "0.5000", "692", "0.7000", "693", "0.3000", "694", "0.5000", "695", "0.9000",
      "696", "0.6000", "697", "0.3000", "698", "0.3000", "699", "0.7000", "700", "0.7000" };

  private static String[] sBm25BaseRawAP = new String[] {
      "601", "0.5441", "602", "0.2755", "603", "0.3273", "604", "0.8168", "605", "0.0713",
      "606", "0.4982", "607", "0.1746", "608", "0.0645", "609", "0.3383", "610", "0.0170",
      "611", "0.2175", "612", "0.5672", "613", "0.1909", "614", "0.1817", "615", "0.0715",
      "616", "0.8164", "617", "0.2511", "618", "0.2063", "619", "0.5921", "620", "0.0799",
      "621", "0.3915", "622", "0.0512", "623", "0.2854", "624", "0.2576", "625", "0.0276",
      "626", "0.1267", "627", "0.0109", "628", "0.2449", "629", "0.1424", "630", "0.7024",
      "631", "0.1751", "632", "0.2144", "633", "0.5022", "634", "0.7553", "635", "0.5225",
      "636", "0.1364", "637", "0.4677", "638", "0.0375", "639", "0.1136", "640", "0.3195",
      "641", "0.3270", "642", "0.1531", "643", "0.4771", "644", "0.2765", "645", "0.6010",
      "646", "0.3262", "647", "0.2067", "648", "0.0824", "649", "0.7240", "650", "0.0986",
      "651", "0.0521", "652", "0.3200", "653", "0.5812", "654", "0.1926", "655", "0.0017",
      "656", "0.5236", "657", "0.3836", "658", "0.1365", "659", "0.2991", "660", "0.6603",
      "661", "0.6059", "662", "0.6554", "663", "0.4316", "664", "0.5192", "665", "0.2212",
      "666", "0.0060", "667", "0.3441", "668", "0.3811", "669", "0.1573", "670", "0.1019",
      "671", "0.3157", "672", "0.0000", "673", "0.2703", "674", "0.1413", "675", "0.2656",
      "676", "0.2868", "677", "0.9182", "678", "0.1751", "679", "0.8722", "680", "0.0615",
      "681", "0.1297", "682", "0.2353", "683", "0.0316", "684", "0.0000", "685", "0.3065",
      "686", "0.3040", "687", "0.2010", "688", "0.1059", "689", "0.0073", "690", "0.0046",
      "691", "0.3800", "692", "0.4351", "693", "0.3423", "694", "0.4735", "695", "0.3155",
      "696", "0.3306", "697", "0.1510", "698", "0.3768", "699", "0.4976", "700", "0.4617" };

  private static String[] sBm25BaseRawP10 = new String[] {
      "601", "0.3000", "602", "0.3000", "603", "0.5000", "604", "0.6000", "605", "0.2000",
      "606", "0.4000", "607", "0.3000", "608", "0.1000", "609", "0.6000", "610", "0.0000",
      "611", "0.3000", "612", "0.7000", "613", "0.2000", "614", "0.1000", "615", "0.1000",
      "616", "1.0000", "617", "0.6000", "618", "0.4000", "619", "0.8000", "620", "0.1000",
      "621", "0.8000", "622", "0.1000", "623", "0.6000", "624", "0.4000", "625", "0.1000",
      "626", "0.0000", "627", "0.1000", "628", "0.4000", "629", "0.2000", "630", "0.3000",
      "631", "0.1000", "632", "0.6000", "633", "1.0000", "634", "0.8000", "635", "0.8000",
      "636", "0.1000", "637", "0.8000", "638", "0.1000", "639", "0.2000", "640", "0.4000",
      "641", "0.5000", "642", "0.2000", "643", "0.4000", "644", "0.3000", "645", "0.9000",
      "646", "0.4000", "647", "0.5000", "648", "0.1000", "649", "1.0000", "650", "0.2000",
      "651", "0.2000", "652", "0.8000", "653", "0.6000", "654", "0.4000", "655", "0.0000",
      "656", "0.7000", "657", "0.5000", "658", "0.3000", "659", "0.2000", "660", "0.9000",
      "661", "0.8000", "662", "0.9000", "663", "0.5000", "664", "0.6000", "665", "0.3000",
      "666", "0.0000", "667", "0.7000", "668", "0.6000", "669", "0.1000", "670", "0.2000",
      "671", "0.5000", "672", "0.0000", "673", "0.5000", "674", "0.2000", "675", "0.3000",
      "676", "0.3000", "677", "0.8000", "678", "0.4000", "679", "0.6000", "680", "0.3000",
      "681", "0.4000", "682", "0.6000", "683", "0.2000", "684", "0.0000", "685", "0.4000",
      "686", "0.5000", "687", "0.8000", "688", "0.3000", "689", "0.0000", "690", "0.0000",
      "691", "0.5000", "692", "0.7000", "693", "0.6000", "694", "0.6000", "695", "0.9000",
      "696", "0.7000", "697", "0.3000", "698", "0.4000", "699", "0.7000", "700", "0.6000" };

  @Test
  public void runRegression() throws Exception {
    String[] params = new String[] {
            "data/trec/run.robust04.nonpositional.baselines.xml",
            "data/trec/queries.robust04.xml" };

    FileSystem fs = FileSystem.getLocal(new Configuration());

    BatchQueryRunner qr = new BatchQueryRunner(params, fs);
    long start = System.currentTimeMillis();
    qr.runQueries();
    long end = System.currentTimeMillis();
    LOG.info("Total query time: " + (end - start) + "ms");

    verifyAllResults(qr.getModels(), qr.getAllResults(), qr.getDocnoMapping(),
        new Qrels("data/trec/qrels.robust04.noCRFR.txt"));
  }

  public static void verifyAllResults(Set<String> models,
      Map<String, Map<String, Accumulator[]>> results, DocnoMapping mapping, Qrels qrels) {
    Map<String, Map<String, Float>> AllModelsAPScores = Maps.newHashMap();
    AllModelsAPScores.put("robust04-dir-base", loadScoresIntoMap(sDirBaseRawAP));
    AllModelsAPScores.put("robust04-bm25-base", loadScoresIntoMap(sBm25BaseRawAP));

    Map<String, Map<String, Float>> AllModelsP10Scores = Maps.newHashMap();
    AllModelsP10Scores.put("robust04-dir-base", loadScoresIntoMap(sDirBaseRawP10));
    AllModelsP10Scores.put("robust04-bm25-base", loadScoresIntoMap(sBm25BaseRawP10));

    for (String model : models) {
      LOG.info("Verifying results of model \"" + model + "\"");
      verifyResults(model, results.get(model),
          AllModelsAPScores.get(model), AllModelsP10Scores.get(model), mapping, qrels);
      LOG.info("Done!");
    }
  }

  private static void verifyResults(String model, Map<String, Accumulator[]> results,
      Map<String, Float> apScores, Map<String, Float> p10Scores, DocnoMapping mapping,
      Qrels qrels) {
    float apSum = 0, p10Sum = 0;
    for (String qid : results.keySet()) {
      float ap = (float) RankedListEvaluator.computeAP(results.get(qid), mapping,
          qrels.getReldocsForQid(qid));

      float p10 = (float) RankedListEvaluator.computePN(10, results.get(qid), mapping,
          qrels.getReldocsForQid(qid));

      apSum += ap;
      p10Sum += p10;

      LOG.info("verifying qid " + qid + " for model " + model);
      assertEquals(apScores.get(qid), ap, 10e-6);
      assertEquals(p10Scores.get(qid), p10, 10e-6);
    }

    // One topic didn't contain qrels, so trec_eval only picked up 99 topics.
    float MAP = (float) RankedListEvaluator.roundTo4SigFigs(apSum / 99f);
    float P10Avg = (float) RankedListEvaluator.roundTo4SigFigs(p10Sum / 99f);

    if (model.equals("robust04-dir-base")) {
      assertEquals(0.3063, MAP, 10e-5);
      assertEquals(0.4424, P10Avg, 10e-5);
    } else if (model.equals("robust04-bm25-base")) {
      assertEquals(0.3033, MAP, 10e-5);
      assertEquals(0.4283, P10Avg, 10e-5);
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Robust04_NonPositional_Baselines.class);
  }
}
