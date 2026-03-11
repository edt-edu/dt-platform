package utils;

public class MachineId {
    // Fixed prefix for all measurements
    private static final String MEASUREMENT_PREFIX = "measurements";

    private final int islandNumber;
    private final MachineIdBuilder.ComponentType componentType;
    private final String prefix;
    private final String formattedComponentId;

    public MachineId(
            String prefix,
            int islandNumber,
            MachineIdBuilder.ComponentType componentType,
            int componentId
    ) {
        this.islandNumber = islandNumber;
        this.componentType = componentType;
        this.formattedComponentId = String.format("%02d", componentId);
        this.prefix = prefix;

    }

    public enum ValueType {
        Sensor("input", "Sens"),
        Actuator("output", "Act");

        private final String pathName;
        private final String topicPrefix;

        ValueType(String pathName, String topicPrefix) {
            this.pathName = pathName;
            this.topicPrefix = topicPrefix;
        }
    }


    public String getTopic(ValueType type, String valueName) {
        String baseId = getBaseId();
        return String.format("%s/%s/%s/%s%s%s",
                baseId,
                MEASUREMENT_PREFIX,
                type.pathName,
                componentType.getTopicPrefix(),
                type.topicPrefix,
                valueName
        );

    }

    public String getBaseId() {
        return String.format("%s/Island %d/%s/I%d%s%s",
                prefix,
                islandNumber,
                componentType.getValue(),
                islandNumber,
                componentType.getValue(),
                formattedComponentId);
    }
}
