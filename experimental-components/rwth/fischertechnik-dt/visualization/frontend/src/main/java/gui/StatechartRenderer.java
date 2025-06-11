package gui;

import fischertechnikvisualization.State;
import fischertechnikvisualization.Statechart;
import fischertechnikvisualization.Transition;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component()
public class StatechartRenderer extends StatechartRendererTOP {

  @Override
  public String getGraphvizRenderer0DotFileContent() {
    StringBuilder res = new StringBuilder();
    Statechart sc = getStatechart();
    res.append("digraph ").append(sc.getName()).append("{\n");
    res.append("node [shape=point,label=\"\"]ENTRY,EXIT;\n");
    res.append("node [shape=circle];\n");

    for (State state : sc.getStatesList()) {
      res.append(state.getLabel()).append("[label=\"").append(state.getLabel()).append("\"");

      if(state.getColor().isPresent()){
        res.append(", color=\"").append(state.getColor().get()).append("\"");
      }

      res.append("];\n");
      if(state.isInitialState()){
        res.append("ENTRY -> ").append(state.getLabel()).append(";\n");
      }

      if(state.isFinalState()){
        res.append(state.getLabel()).append(" -> EXIT;\n");
      }
    }

    for (Transition transition : sc.getTransitionsList()) {
      // JSweet workaround: no nested access with gems
      State source = transition.getSource();
      State target = transition.getTarget();
      res.append(source.getLabel()).append(" -> ").append(target.getLabel());

      List<String> attributes = new ArrayList<>();

      if (transition.getLabel().isPresent()) {
        attributes.add("label=\"" + transition.getLabel().get() + "\"");
      }

      if (transition.getColor().isPresent()) {
        attributes.add("color=\"" + transition.getColor().get() + "\"");
      }

      if (!attributes.isEmpty()) {
        res.append("[").append(String.join(", ", attributes)).append("]");
      }
      res.append(";\n");
    }

    res.append("\n}");
    return res.toString();
  }
}
