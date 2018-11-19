
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jfoley
 */
public class RunAlphabetQueries {
  public static String OITUserName = "jshepherd";

  public static void main(String[] args) throws IOException {
    String inputFile = args.length > 0 ? args[0] : "C:\\Users\\Jonathan\\Documents\\NetBeansProjects\\p4\\src\\shakespeare-scenes.json.gz";
    SceneIndex index = LoadData.loadIndex(inputFile);

    List<Query> queries = Arrays.asList(
        new Query("Q1", "the", "king", "queen", "royalty"),
        new Query("Q2", "servant", "guard", "soldier"),
        new Query("Q3", "hope", "dream", "sleep"),
        new Query("Q4", "ghost", "spirit"),
        new Query("Q5", "fool", "jester", "player"),
        new Query("Q6", "to", "be", "or", "not", "to", "be")
    );

    String method = "alphabetic";
    /*for (Query query : queries) {
      List<SceneId> hits = new ArrayList<>(index.findScenesWithAny(query.terms));
      Collections.sort(hits); // sort alphabetically.
      for (int i = 0; i < hits.size(); i++) {
        SceneId scene = hits.get(i);
        int rank = i+1;
        System.out.printf("%s skip %-40s %d %.3f %s-%s\n", query.id, scene, rank, 1.0 / ((double) rank), OITUserName, method);
      }
    }*/
        
    method = "bm25";
    double k1 = 1.2;
    double k2 = 100.0;
    double b = 0.75;
    int N = 748;//N = num of docs in collection
    int ri = 0;//ri = number of relevant documents containing term
    int wordsincollection = 0;
    for (Map.Entry<String, Map<SceneId, Integer>> entry : index.counts.entrySet())
        wordsincollection += entry.getValue().size();
    double avdl = (double)wordsincollection / (double)N;
    
    for (Query query : queries) {
      List<SceneId> hits = new ArrayList<>(index.findScenesWithAny(query.terms));
      int R = 0;//R = num of relevant docs
      double rank[] = new double[hits.size()];
      for(int j = 0; j < hits.size(); j++){
        double dl = (double) LoadData.scenes.get(hits.get(j).raw).size();
        double K = k1 * ((1.0-b) + (b * dl / avdl));
        double ni = 0.0;//ni = num of docs that contain term
        double fi = 0.0, qfi = 0.0;//fi = term freq in doc,qfi = freq of term in query
        
        rank[j] = 0.0;
        for(int k = 0; k < query.terms.size(); k++){
            ni = (double) index.findScenesWithAny(Arrays.asList(query.terms.get(k))).size();
            for (int l = 0; l < query.terms.size(); l++){
                if(query.terms.get(l).equals(query.terms.get(k)))
                    qfi += 1.0;
            }
            List<String> s = LoadData.scenes.get(hits.get(j).raw);
            for (int l = 0; l < s.size(); l++){
                if(s.get(l).equals(query.terms.get(k)))
                    fi += 1.0;
            }
            rank[j] += Math.log((((ri + 0.5) / (R - ri + 0.5)) / ((ni - ri + 0.5) / (N - ni - R + ri + 0.5))) * (((k1 + 1) * fi) / (K + fi)) * (((k2 + 1) * qfi) / (k2 + qfi)));
        }
      }
      
      //sort
      boolean t = true;
      while(t){
          t = false;
          for(int p = 0; p < hits.size()- 1; p++){
              if (rank[p] < rank[p+1]){
                  t = true;
                  double temp = rank[p];
                  rank[p] = rank[p+1];
                  rank[p+1] = temp;
                  SceneId temps = hits.get(p);
                  hits.set(p, hits.get(p+1));
                  hits.set(p+1, temps);
              }
          }
      }
      
      for (int i = 0; i < hits.size(); i++) {
        SceneId scene = hits.get(i);
        System.out.printf("%s skip %-40s %d %.3f %s-%s\n", query.id, scene, i+1, rank[i], OITUserName, method);
      }
    }
    
    
    method = "ql";
    double mu = 1500.0;
    for (Query query : queries) {
      List<SceneId> hits = new ArrayList<>(index.findScenesWithAny(query.terms));
      double rank[] = new double[hits.size()];
      for(int j = 0; j < hits.size(); j++){
        double D = (double) LoadData.scenes.get(hits.get(j).raw).size();
        double C = wordsincollection;
        double fqiD = 0.0, cqi = 0.0;
        
        rank[j] = 0.0;
        for(int k = 0; k < query.terms.size(); k++){
            cqi = index.counts.get(query.terms.get(k)).size();
            List<String> s = LoadData.scenes.get(hits.get(j).raw);
            for (int l = 0; l < s.size(); l++){
                if(s.get(l).equals(query.terms.get(k)))
                    fqiD += 1.0;
            }
            rank[j] += Math.log((fqiD + (mu * (cqi / C))) / (D + mu));
        }
      }
      
      //sort
      boolean t = true;
      while(t){
          t = false;
          for(int p = 0; p < hits.size()- 1; p++){
              if (rank[p] < rank[p+1]){
                  t = true;
                  double temp = rank[p];
                  rank[p] = rank[p+1];
                  rank[p+1] = temp;
                  SceneId temps = hits.get(p);
                  hits.set(p, hits.get(p+1));
                  hits.set(p+1, temps);
              }
          }
      }
      
      for (int i = 0; i < hits.size(); i++) {
        SceneId scene = hits.get(i);
        System.out.printf("%s skip %-40s %d %.3f %s-%s\n", query.id, scene, i+1, rank[i], OITUserName, method);
      }
    }
    
    
  }
}
