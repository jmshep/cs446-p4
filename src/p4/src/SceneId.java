/**
* @author jfoley
*/
public class SceneId implements Comparable<SceneId> {
  String raw;
  String play;
  int act;
  int scene;
  /** input is play:act.scene */
  public SceneId(String input) {
    raw = input;
    String[] splitColon = input.split(":");
    play = splitColon[0];
    String[] splitPeriod = splitColon[1].split("\\.");
    act = Integer.parseInt(splitPeriod[0]);
    scene = Integer.parseInt(splitPeriod[1]);
  }
  @Override
  public String toString() {
    return raw;
  }
  @Override
  public int hashCode() {
    return raw.hashCode();
  }
  @Override
  public boolean equals(Object o) {
    return o instanceof SceneId && ((SceneId) o).raw.equals(raw);
  }

  @Override
  public int compareTo(SceneId o) {
    return raw.compareTo(o.raw);
  }
}
