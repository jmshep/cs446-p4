import java.util.*;

/**
 * @author jfoley
 */
public class SceneIndex {
  public Map<String, Map<SceneId, Integer>> counts = new HashMap<>();
  /** Find documents which contain any of the given terms. */
  public Set<SceneId> findScenesWithAny(List<String> orTerms) {
    Set<SceneId> output = new HashSet<>();
    for (String orTerm : orTerms) {
      output.addAll(counts.get(orTerm).keySet());
    }
    return output;
  }

  /** Insert term, document, and increment count. */
   public void process(String token, SceneId scene) {
    Map<SceneId, Integer> postings = counts.get(token);
    if(postings == null) {
      postings = new HashMap<>();
      counts.put(token, postings);
    }
    Integer previousValue = postings.get(scene);
    if(previousValue == null) {
      previousValue = 0;
    }
    postings.put(scene, previousValue+1);
  }

}
