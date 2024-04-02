package classes

data class WafflePiState(
    var batteryState: BatteryState?,
    var movement: Movement?,
    var orientation: Orientation?,
    var ros: Ros?,
    var wheelState: WheelState?,
    var distance: Double?
)