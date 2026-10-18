import java.util.ArrayList;

public class EVDataSet {

  private ArrayList<ElectricVehicle> vehicleList;

  public EVDataSet() { /* implementation not shown */
    this.vehicleList = new ArrayList<ElectricVehicle>();
    this.vehicleList.add(new ElectricVehicle("Eco 6", 200));
    this.vehicleList.add(new ElectricVehicle("Hallo", 300));
    this.vehicleList.add(new ElectricVehicle("QTK2", 250));
    this.vehicleList.add(new ElectricVehicle("Nu EV", 270));
    this.vehicleList.add(new ElectricVehicle("QTK2", 220));
    this.vehicleList.add(new ElectricVehicle("Ekstat", 190));
  }

  /**
   * Returns a list of the model names of electric vehicles whose range, in miles,
   * is greater than target, without any duplicate model names Preconditions:
   * vehicleList is not null. No elements of vehicleList are null. Postcondition:
   * vehicleList is unchanged.
   */
  public ArrayList<String> getHighRangeVehicles(int target) { /* to be implemented */
    ArrayList<String> ret = new ArrayList<>();
    for (ElectricVehicle vehicle : vehicleList) {
      if (vehicle.getRange() > target) {

        if (!ret.contains(vehicle.getModelName())) {
          ret.add(vehicle.getModelName());
        }
      }
    }

    return ret;
  }

  /*
   * There may be instance variables, constructors, and methods that are not
   * shown.
   */

}
