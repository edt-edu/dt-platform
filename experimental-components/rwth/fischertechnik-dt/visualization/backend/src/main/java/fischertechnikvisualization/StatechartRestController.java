package fischertechnikvisualization;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.*;
import jsweet.lang.Erased;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Example requests
 * Create statechart
 curl --header "Content-Type: application/json" \
 --request POST \
 --data '{"name":"sc2","states":["a", "b"], "transitions":[{"source":"a", "target":"b","label":"casdasd"}]}' \
 http://localhost:8081/umlp/api/api/statecharts/

 * Update state color
 curl --header "Content-Type: application/json" \
 --request POST \
 --data '"green"' \
 http://localhost:8081/umlp/api/api/statecharts/sc2/states/a/color

 * Update transition color
 curl --header "Content-Type: application/json" \
 --request POST \
 --data '"red"' \
 http://localhost:8081/umlp/api/api/statecharts/sc2/transitions/a/b/color

 */
@Erased
@RestController
@RequestMapping("/api/statecharts/")
public class StatechartRestController {

  public StatechartRestController() {
    System.out.println("Creating StatechartRestController");
  }

  @GetMapping
  public List<Long> getAllStatechartGemIds() {
    return FischertechnikVisualizationManager.getStatechartList().stream().map(Statechart::getGemId).collect(Collectors.toList());
  }

  @PostMapping("/")
  public boolean createStatechart(@RequestBody String json) {
    JsonElement e = JsonParser.parse(json);
    if (!e.isJsonObject()) {
      return false;
    }
    JsonObject obj = e.getAsJsonObject();

    StatechartBuilder statechart = FischertechnikVisualizationManager.statechartBuilder();

    // Name
    statechart.name(obj.getStringMember("name"));

    // States
    Map<String, State> stateMap = new HashMap<>();
    for (JsonElement state : obj.getArrayMember("states")) {
      String stateName = state.getAsJsonString().getValue();
      State res = FischertechnikVisualizationManager.stateBuilder().label(stateName).initialState(false).finalState(false).build().get();
      stateMap.put(stateName, res);
      statechart.statesAdd(res);
    }

    // Transitions
    for (JsonElement transitionElem : obj.getArrayMember("transitions")) {
      JsonObject transitionObj = transitionElem.getAsJsonObject();
      TransitionBuilder transition = FischertechnikVisualizationManager.transitionBuilder()
          .source(stateMap.get(transitionObj.getStringMember("source")))
          .target(stateMap.get(transitionObj.getStringMember("target")));

      if (transitionObj.hasStringMember("label")) {
        transition.label(Optional.of(transitionObj.getStringMember("label")));
      }
      statechart.transitionsAdd(transition);
    }

    // Initial/final markers
    if (obj.hasStringMember("initial")) {
      stateMap.get(obj.getStringMember("initial")).setInitialState(true);
    }
    if (obj.hasArrayMember("final")) {
      for (JsonElement elem : obj.getArrayMember("final")) {
        String s = elem.getAsJsonString().getValue();
        stateMap.get(s).setFinalState(true);
      }
    }

    // build
    return statechart.build().isPresent();
  }

  @PostMapping("/{statechartName}/states/{stateLabel}/color")
  public boolean setStateColor(
      @RequestBody String json,
      @PathVariable String statechartName,
      @PathVariable String stateLabel
  ) {

    Statechart statechart = findStatechartByName(statechartName);
    if (statechart == null) {
      return false;
    }

    State state = null;
    for (State s : statechart.getStatesList()) {
      if (s.getLabel().equals(stateLabel)) {
        state = s;
        break;
      }
    }

    if (state == null) {
      return false;
    }

    JsonElement elem = JsonParser.parse(json);
    if (elem.isJsonNull()) {
      state.setColor(Optional.empty());
      return true;
    } else if (elem.isJsonString()) {
      state.setColor(Optional.of(elem.getAsJsonString().getValue()));
      return true;
    }

    return false;
  }

  @PostMapping("/{statechartName}/transitions/{source}/{target}/color")
  public boolean setTransitionColor(
      @RequestBody String json,
      @PathVariable String statechartName,
      @PathVariable String source,
      @PathVariable String target
  ) {
    Statechart statechart = findStatechartByName(statechartName);
    if (statechart == null) {
      return false;
    }

    Transition transition = null;
    for (Transition t : statechart.getTransitionsList()) {
      if (t.getSource().getLabel().equals(source) && t.getTarget().getLabel().equals(target)) {
        transition = t;
        break;
      }
    }

    if (transition == null) {
      return false;
    }

    JsonElement elem = JsonParser.parse(json);
    if (elem.isJsonNull()) {
      transition.setColor(Optional.empty());
      return true;
    } else if (elem.isJsonString()) {
      transition.setColor(Optional.of(elem.getAsJsonString().getValue()));
      return true;
    }

    return false;
  }

  private static Statechart findStatechartByName(String statechartName) {
    Statechart statechart = null;
    for (Statechart s : FischertechnikVisualizationManager.getStatechartList()) {
      if (s.getName().equals(statechartName)) {
        statechart = s;
        break;
      }
    }
    return statechart;
  }
}
