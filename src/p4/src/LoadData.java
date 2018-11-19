
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author jfoley
 */





public class LoadData {
  public static Map<String, List<String>> scenes = new HashMap<>();
  public static SceneIndex loadIndex(String inputFilePath) throws IOException {
    SceneIndex out = new SceneIndex();
    try (BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputFilePath)), "UTF-8"))) {
      JSONObject json = (JSONObject) JSONValue.parse(rdr);
      JSONArray docs = (JSONArray) json.get("corpus");

      for (Object o : docs) {
        JSONObject doc = (JSONObject) o;
        SceneId scene = new SceneId((String) doc.get("sceneId"));

        for (String token : ((String) doc.get("text")).split("\\s+")) {
          if(token.isEmpty()) continue;
          out.process(token, scene);
          List<String> temp = new ArrayList<String>();
          if(scenes.containsKey(scene.raw)){
            temp = scenes.get(scene.raw);
            temp.add(token);
            scenes.replace(scene.raw, temp);
          }
          else{
            temp.add(token);
            scenes.put(scene.raw, temp);
          }
        }
      }

    }
    return out;
  }
}
