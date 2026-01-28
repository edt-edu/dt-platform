package utils;

/**
 * Builder for MQTT topics with the format: PLC/Island #/ComponentType/ComponentType##
 * Where:
 * - PLC is fixed
 * - Island # is a variable island number
 * - ComponentType is one of the predefined component types
 * - ## is a two-digit ID (01-99)
 */
public class MachineIdBuilder {
    // Fixed prefix for all topics
    private static final String PLC_PREFIX = "PLC";

    /**
     * Enum representing the valid component types
     */
    public enum ComponentType {
        VACUUM_GRIPPER("VacuumGripper", "vacuum"),
        CONVEYOR_BELT("ConveyorBelt", "conveyor"),
        MULTI_PROCESSING("MultiProcessing", "multiProcessing"),
        SORTING_LINE("SortingLine", "sortingLine"),
        HIGH_BAY("HighBay", "highbay"),
        PUNCHING_MACHINE("PunchingMachine", "punchingMachine"),
        INDEXED_LINE("IndexedLine", "indexedLine");
        //TODO: check names with data
        // THREE_D_GRIPPER("ThreeDGripper", "threeDGripper"),;

        private final String value;
        private final String topicPrefix;

        ComponentType(String value, String topicPrefix) {
            this.value = value;
            this.topicPrefix = topicPrefix;
        }

        public String getValue() {
            return value;
        }

        public String getTopicPrefix() {
            return topicPrefix;
        }
    }

    private Integer islandNumber;
    private ComponentType componentType;
    private Integer componentId;

    /**
     * Creates a new utils.MachineIdBuilder with no default values.
     * All values must be explicitly set before calling build().
     */
    public MachineIdBuilder() {
        // No default values
    }

    /**
     * Sets the island number.
     * 
     * @param islandNumber The island number (must be positive)
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if the island number is not positive
     */
    public MachineIdBuilder withIslandNumber(int islandNumber) {
        if (islandNumber <= 0) {
            throw new IllegalArgumentException("Island number must be positive");
        }
        this.islandNumber = islandNumber;
        return this;
    }

    /**
     * Sets the component type using the enum value.
     * 
     * @param componentType The component type enum value
     * @return This builder instance for method chaining
     */
    public MachineIdBuilder withComponentType(ComponentType componentType) {
        this.componentType = componentType;
        return this;
    }

    /**
     * Sets the component ID.
     * 
     * @param componentId The component ID (must be between 1 and 99)
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if the component ID is not between 1 and 99
     */
    public MachineIdBuilder withComponentId(int componentId) {
        if (componentId < 1 || componentId > 99) {
            throw new IllegalArgumentException("Component ID must be between 1 and 99");
        }
        this.componentId = componentId;
        return this;
    }

    /**
     * Builds the MQTT topic string with the format: PLC/Island #/ComponentType/ComponentType##
     * 
     * @return The formatted MQTT topic string
     * @throws IllegalStateException if any required values (islandNumber, componentType, componentId) are not set
     */
    public MachineId build() {
        // Check if all required values are set
        if (islandNumber == null) {
            throw new IllegalStateException("Island number must be set before building");
        }
        if (componentType == null) {
            throw new IllegalStateException("Component type must be set before building");
        }
        if (componentId == null) {
            throw new IllegalStateException("Component ID must be set before building");
        }


        return new MachineId(
                PLC_PREFIX,
                islandNumber,
                componentType,
                componentId
        );
    }

}
