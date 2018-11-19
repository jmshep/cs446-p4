import java.util.Arrays;
import java.util.List;

/**
 * @author jfoley
 */
public class Query {
  public final List<String> terms;
  public final String id;

  public Query(String id, String... terms) {
    this.id = id;
    this.terms = Arrays.asList(terms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(id);
    for (String term : terms) {
      sb.append(' ').append(term);
    }
    return sb.toString();
  }
}
