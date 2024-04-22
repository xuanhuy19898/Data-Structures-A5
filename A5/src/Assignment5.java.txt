/**I, Xuan Huy Pham, 000899551, certify that this material is my original work.
 * No other person's work has been used without suitable acknowledgment
 * and I have not made my work available to anyone else. */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Assignment5 {
    public static void main(String[] args) {
        int expressLines;
        int normalLines;
        int threshold;
        int totalCustomers;
        int[] customers;
        //read the data from data text file
        try {
            File file = new File("src/CustomerData.txt");
            Scanner scanner = new Scanner(file);
            expressLines = scanner.nextInt(); //expressline, normallines and threshold are in the same line
            normalLines = scanner.nextInt();
            threshold = scanner.nextInt();
            scanner.nextLine();
            totalCustomers = scanner.nextInt();//totalCustomers is in the next line
            scanner.nextLine();
            customers = new int[totalCustomers];//initialize array to store customer data
            for (int i=0;i<totalCustomers;i++) {
                customers[i] = scanner.nextInt();
            }
            scanner.close();
        } catch (FileNotFoundException e) { //handle file not found exception
            System.out.println("File not found: "+e.getMessage());
            return;
        }

        //create the CheckOutSystem instance with expressLines, normalLines and threshold
        CheckOutSystem store = new CheckOutSystem(expressLines, normalLines, threshold);

        //part A - checkout lines and how much time each line takes
        System.out.println("PART A - Checkout lines and time estimates for each line");
        System.out.println("=========================================================");
        for (int i = 0; i < totalCustomers; i++) {
            Customer customer = new Customer(customers[i]);//create a Customer object
            store.enqueueCustomer(customer); //enqueue the customer
        }
        store.printCheckout();

        //part B - number of customers in line after every 30s
        System.out.println("\nPART B - Number of customers in line after every 30s");
        System.out.println("=========================================================");
        store.runCheckout();
    }

    static class CheckOutSystem {
        private LinkedQueue<Customer>[] expressLines;
        private LinkedQueue<Customer>[] normalLines;
        private int threshold;

        /**
         * constructs CheckoutSystem instance with the specified number of lines from data text file
         * including expressLine, normal Line and threshold
         * @param expressLinesNum number of express lines
         * @param normalLinesNum number of normal lines
         * @param threshold the threshold for express checkout, if ni < threshold, customer can switch to faster lane
         */
        public CheckOutSystem(int expressLinesNum, int normalLinesNum, int threshold) {
            this.threshold = threshold;
            expressLines = new LinkedQueue[expressLinesNum];//initialize the array of express lines
            normalLines = new LinkedQueue[normalLinesNum];//same but of normal lines
            //initialize each expres sline as an empty queue
            for (int i = 0; i < expressLinesNum; i++) {
                expressLines[i] = new LinkedQueue<>();
            }
            //initialize each normal line as an empty queue
            for (int i = 0; i < normalLinesNum; i++) {
                normalLines[i] = new LinkedQueue<>();
            }
        }

        /**
         * this method is to add a customer to the suitable line based on the threshold
         * @param customer the one to be added
         */
        public void enqueueCustomer(Customer customer) {
            //check if customer qualifies for express line (ni < threshold)
            if (customer.getItems() <= threshold) {
                int shortestLine = findShortestLine(expressLines);//find the shortest express line
                expressLines[shortestLine].enqueue(customer);//enqueue customer to that line
            } else {
                //or add customer to the shortest normal line
                int shortestLine = findShortestLine(normalLines);
                normalLines[shortestLine].enqueue(customer);
            }
        }

        /**
         * this method is to print the checkout lines, including their estimated times and total time
         */
        public void printCheckout() {
            for (int i = 0; i < expressLines.length; i++) {
                System.out.println("CheckOut(Express) #" + (i + 1) + " (Est Time = " + getEstimatedTime(expressLines[i]) + " s) = " + expressLines[i]);
            }
            for (int i = 0; i < normalLines.length; i++) {
                System.out.println("CheckOut(Normal) #" + (i + 1) + " (Est Time = " + getEstimatedTime(normalLines[i]) + " s) = " + normalLines[i]);
            }
            System.out.println("Time to clear store of all customers = " + getTotalTime() + " s");
        }

        /**
         * this method is to find the shortest line for customer
         * @param lines the lines to be checked
         * @return the index of the shortest line
         */
        private int findShortestLine(LinkedQueue<Customer>[] lines) {
            int shortestLine = 0;
            int minSize = lines[0].size();//initialize the minimum size with the size of the first line
            //compare the size of each line with the minimum size then update it
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].size() < minSize) {
                    shortestLine = i;
                    minSize = lines[i].size();
                }
            }
            return shortestLine; //index of the shortest line
        }



        /**
         * this method is to get the estimated time for checkout line
         * @param line the line
         * @return the estimated time
         */
        private int getEstimatedTime(LinkedQueue<Customer> line) {
            int estimatedTime = 0;
            //create a copy of the line
            //and calculate the estimated time by dequeuing each customer and summing their time
            LinkedQueue<Customer> copy = new LinkedQueue<>();
            while (!line.isEmpty()) {
                Customer customer = line.dequeue();//dequeue customer from the line
                estimatedTime += customer.getTime();//add the checkout time to the estimated time
                copy.enqueue(customer);//enqueue cusotmer to the copy of the line
            }
            //restore the original order of customers by enqueuing them back to the original line
            while (!copy.isEmpty()) {
                line.enqueue(copy.dequeue());
            }
            return estimatedTime;
        }

        /**
         * this method is to get total time needed for checkout
         * @return the total time
         */
        private int getTotalTime() {
            int total = 0;
            //sum the estimated times for each checkout line
            for (LinkedQueue<Customer> line : expressLines) { //for express lines
                total += getEstimatedTime(line);
            }
            for (LinkedQueue<Customer> line : normalLines) { //for normal lines
                total += getEstimatedTime(line);
            }
            return total;
        }

        /**
         * this method is to run the checkout process of part B
         */
        public void runCheckout() {
            int currentTime = 0; // Start from t(s) = 0
            System.out.println("t(s)\tLine 1(E)\tLine 2(E)\tLine 1\tLine 2\tLine 3" +
                                "\tLine 4\tLine 5\tLine 6\tLine 7\tLine 8");
            boolean firstIteration = true; //flag to skip the first iteration
            while (!isEmpty()) {
                if (!firstIteration && currentTime % 30 == 0) { //print the output every 30 seconds after the first iteration
                    System.out.println(currentTime + "\t\t" + expressLines[0].size() + "\t\t\t" + expressLines[1].size() + "\t\t\t" + normalLines[0].size()
                            + "\t\t" + normalLines[1].size() + "\t\t" + normalLines[2].size() + "\t\t" + normalLines[3].size() + "\t\t"
                            + normalLines[4].size() + "\t\t" + normalLines[5].size() + "\t\t" + normalLines[6].size()+ "\t\t" + normalLines[7].size());
                }
                //remove customers whose service time has elapsed
                //check if the line is not empty and check if the time of the first customer in the line has elapsed
                //then remove the customer from the front of the line
                for (LinkedQueue<Customer> line : expressLines) {//for express lines
                    if (!line.isEmpty()) {
                        if (line.peek().getTime() <= currentTime) {
                            line.dequeue();
                        }
                    }
                }
                for (LinkedQueue<Customer> line : normalLines) { //for normal lines
                    if (!line.isEmpty()) {
                        if (line.peek().getTime() <= currentTime) {
                            line.dequeue();
                        }
                    }
                }

                firstIteration = false;
                currentTime += 30;//increment time by 30s
            }
        }

        /**
         * this method is to check if all checkout lines are empty
         * @return true if all lines are empty, otherwise false
         */
        private boolean isEmpty() {
            for (LinkedQueue<Customer> line : expressLines) {
                if (!line.isEmpty()) {
                    return false;
                }
            }
            for (LinkedQueue<Customer> line : normalLines) {
                if (!line.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }
}
