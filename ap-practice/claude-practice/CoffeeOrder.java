public class CoffeeOrder {
  private String drinkName;
  private char size;
  private double basePrice;

  public CoffeeOrder(String drinkName, char size, double basePrice) {
    this.drinkName = drinkName;
    this.size = size;
    this.basePrice = basePrice;
  }

  public String getDrink() {
    return drinkName;
  }

  public double getPrice() {
    switch (size) {
    case 'M':
      return basePrice + 0.75;
    case 'L':
      return basePrice + 1.50;
    }
    return basePrice;
  }

  public String describe() {
    return String.valueOf(size) + " " + drinkName + ": $" + String.format("%.2f", getPrice());
  }
}
