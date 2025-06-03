package gui;

import fischertechnikvisualization.BooleanTopic;
import fischertechnikvisualization.DoubleTopic;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartDataBuilder;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component()
public class TopicVisualization extends TopicVisualizationTOP {
  @Override
  public GemLineChartData getBooleanLineChartData(BooleanTopic topic) {
    GemLineChartDataBuilder res = new GemLineChartDataBuilder();
    List<String> lables = new ArrayList<>(); // Changed to a list of strings for labels

    List<Integer> points = new ArrayList<>();
    int s = topic.sizeValues();
    int start = Math.max(0, s - 100); // Added start index
    int counter = start; // Added counter for labels
    List<Boolean> last100Values = topic.getValuesList().subList(start, s); // Used start index
    for (Boolean b : last100Values) {
      points.add(b ? 1 : 0);
      lables.add("" + (counter++)); // Generate numerical labels
    }

    System.out.print("Should show points");
    System.out.println(points);

    res.addEntries(
        new GemLineChartEntryBuilder()
            .data(points)
            .label(topic.getTopicName()) // Set the label for the data series
            .build().get()
    );

    res.labels(lables); // Set the generated labels
    GemLineChartData gemLineChartData = res.build().get();
    System.out.println(gemLineChartData);
    return gemLineChartData;
  }

  @Override
  public GemLineChartData getDoubleLineChartData(DoubleTopic topic) {
    GemLineChartDataBuilder res = new GemLineChartDataBuilder();
    List<String> lables = new ArrayList<>();

    List<Integer> points = new ArrayList<>();
    int s = topic.sizeValues();
    int start = Math.max(0, s - 100);
    int counter = start;
    List<Double> last100Values = topic.getValuesList().subList(start, s);
    for (Double d : last100Values) {
      points.add(d.intValue());
      lables.add("" + (counter++));
    }

    System.out.print("Should show points");
    System.out.println(points);

    res.addEntries(
        new GemLineChartEntryBuilder()
            .data(points)
            .label(topic.getTopicName())
            .build().get()
    );

    res.labels(lables);
    GemLineChartData gemLineChartData = res.build().get();
    System.out.println(gemLineChartData);
    return gemLineChartData;
  }
}