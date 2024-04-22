/**I, Xuan Huy Pham, 000899551, certify that this material is my original work.
 * No other person's work has been used without suitable acknowledgment
 * and I have not made my work available to anyone else. */

/**
 * represents a customer in the checkout
 * each customer has a number of items in their cart
 */
public class Customer {
    private int items;

    /**
     * a customer object with number of items
     * @param items the number of items
     */
    public Customer(int items) {
        this.items = items;
    }

    /**
     * return the number of items in customer's cart
     * @return number of items
     */
    public int getItems() {
        return items;
    }

    /**
     * calculate the time for customer based on the number of items they have in their cart
     * @return time
     */
    public int getTime() {
        return 45 + 5 * items; //45s + 5 * (number of items in their cart)
    }

    @Override
    public String toString() {
        return items + "(" + getTime() + " s)";
    }
}
