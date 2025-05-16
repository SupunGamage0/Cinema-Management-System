import java.util.Scanner;

public class CinemaManagement{

    //price constants based on seating rows position

    private static final int front_row_price = 12;
    private static final int middle_row_price = 10;
    private static final int back_row_price = 8;


    //constants representing cinema seating plan of rows and seats

    private static final int number_of_rows = 3;
    private static final int seats_per_row = 16;


    //arrays managing seating status and ticket bookings
    private static final int[][] seats = new int[number_of_rows][seats_per_row];
    private static final Ticket[][] seatTickets = new Ticket[number_of_rows][seats_per_row];
    private static final Ticket[] tickets = new Ticket[number_of_rows * seats_per_row];
    private static int ticketCount = 0; //keeps count of tickets sold

    public static void main(String[] args){
        System.out.println (" Welcome to the London Lumiere...");

        //initialize seating arrangement with all seats that available
        setup_seating();

        //create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        //display the main menu options for user interactions
        display_main_menu();

        //loop to keep the application running until the user chooses to exit
        while (true) {

            //ask the option from the user
            int option = ask_option(scanner);

            //perform actions based on the selected menu option
            switch (option) {
                case 1:
                    buy_ticket(scanner);
                    break;

                case 2:
                    cancel_ticket(scanner);
                    break;

                case 3:
                    print_seating_area();
                    break;

                case 4:
                    find_first_available_seat();
                    break;

                case 5:
                    print_tickets_info();
                    break;

                case 6:
                    search_ticket(scanner);
                    break;

                case 7:
                    sort_tickets();
                    break;

                case 8:
                    System.out.println("Logging out. Thank you for using our service!");
                    scanner.close();
                    return; //exit from the program

                default:
                    System.out.println("Incorrect data. Please choose a correct choice from the menu.");
                    break;
            }
        }
    }

    //set up the seating so that all seats are initially available

    private static void setup_seating() {
        for (int rowIdx = 0; rowIdx < number_of_rows; rowIdx++) {
            for (int seatIdx = 0; seatIdx < seats_per_row; seatIdx++) {
                seats[rowIdx][seatIdx] = 0; //mark 0 if available and 1 if booked
            }
        }
    }

    // main menu options for the user

    private static void display_main_menu() {
        System.out.println("----------------------------------------------------");
        System.out.println(" Please select an option:");
        System.out.println("1) Buy a ticket");
        System.out.println("2) Cancel ticket");
        System.out.println("3) See seating plan");
        System.out.println("4) Find first available seat");
        System.out.println("5) Print tickets information and total price");
        System.out.println("6) Search ticket");
        System.out.println("7) Sort tickets by price");
        System.out.println("8) Exit");
        System.out.println("----------------------------------------------------");
    }

    //to ask a valid option from the user

    private static int ask_option(Scanner scanner){
        while (true) {
            System.out.print("Select an option : ");
            try {
                int option = scanner.nextInt();
                return option;
            } catch (Exception e){
                System.out.println("Incorrect data. Kindly provide a approved numerical value.");
                scanner.nextLine(); // clear the buffer
            }
        }
    }


    //if user used any invalid row or seat number,inform the user about what should choose

    private static int check_input_validity(Scanner scanner, String type, int min, int max){
        while (true) {
            System.out.print("Type " + type + " value from " + min + "-" + max + " : ");
            try {
                int input = scanner.nextInt();
                if (input < min || input > max) {
                    System.out.println("Incorret " + type + " value. Choose a value in the range " + min + "-" + max + ".");
                } else {
                    return input;
                }
            } catch (Exception e) {
                System.out.println("Incorrect data. Kindly provide a approved numerical value.");
                scanner.nextLine(); //reset the input buffer to prepare for new input
            }
        }
    }


    // ticket buying part of cinema

    private static void buy_ticket(Scanner scanner) {
        int row = check_input_validity(scanner, "row", 1, number_of_rows);
        int seat = check_input_validity(scanner, "seat", 1, seats_per_row);

        if(row == -1 || seat == -1) {
            return;
        }
        if (seats[row - 1][seat - 1] == 0) {
            Person person = get_person_details(scanner); // create object to the person class details
            int price = calculate_price_by_row(row);
            Ticket ticket = new Ticket(row, seat, price, person); // create object to the ticket class details

            seatTickets[row - 1][seat - 1] = ticket;
            tickets[ticketCount++] = ticket;
            seats[row - 1][seat - 1] = 1;

            System.out.println("The seat has been successfully booked.");
        } else {
            System.out.println("Sorry. This seat is currently unavailable. Kindly select a different seat.");
        }
        System.out.println(); //Add a gap after the process
    }


    // cancel a ticket that booked before

    private  static void cancel_ticket(Scanner scanner) {
        int row = check_input_validity(scanner, "row", 1, number_of_rows);
        int seat = check_input_validity(scanner, "seat", 1,seats_per_row);

        if (row == -1 || seat == -1) {
            return;

        }

        if (seats[row -1][seat - 1] == 1) {
            seats[row - 1][seat - 1] = 0;
            seatTickets[row - 1][seat - 1] = null;

            for (int i = 0; i < ticketCount; i++) {
                if (tickets[i].getRow() == row && tickets[i].getSeat() == seat) {
                    tickets[i] = tickets[--ticketCount];
                    tickets[ticketCount] = null;
                    break;
                }
            }

            System.out.println("Booking for this seat has been successfully canceled. ");

        } else {
            System.out.println("This seat is not reserved. Kindly choose a different one.");
        }
        System.out.println(); // add a gap after the process
    }


    //display seating plan currently in the cinema

    private static void print_seating_area() {
        System.out.println("***********************");
        System.out.println("*       SCREEN        *");
        System.out.println("***********************");

        for (int rowIdx = 0; rowIdx < number_of_rows; rowIdx++) {
            for(int seatIdx = 0; seatIdx < seats_per_row; seatIdx++) {
                System.out.print(seats[rowIdx][seatIdx] == 0  ? "0" : "X");
                if (seatIdx == 7) {
                    System.out.print(" "); // adding a gap between seat 8 and 9 for seating plan
                }
            }
            System.out.println();
        }
        System.out.println(); // add a gap after the process
    }


    //if user need to know which seat is available first in cinema

    private static void find_first_available_seat() {
        for (int rowIdx = 0; rowIdx < number_of_rows; rowIdx++) {
            for (int seatIdx = 0; seatIdx < seats_per_row; seatIdx++) {
                if (seats[rowIdx][seatIdx] == 0) {
                    System.out.println("First available seat is in Row : " + (rowIdx + 1) + ", Seat : " + (seatIdx + 1));
                    System.out.println();  // add a gap after the process
                    return;

                }
            }
        }
        System.out.println("No seats are currently available. ");
        System.out.println();  // add a gap after the process
    }


    //print all information of the tickets that purchased by the user

    private static void print_tickets_info() {
        int totalPrice = 0;
        System.out.println("Tickets currently sold:");
        for (int i = 0; i < ticketCount; i++) {
            Ticket ticket = tickets[i];
            System.out.println("Ticket " + (i + 1) + ":");
            System.out.println("Row: " + ticket.getRow() + ", Seat: " + ticket.getSeat() + ", Price: £" + ticket.getPrice());
            System.out.println("Person Information:");
            System.out.println("Name: " + ticket.getPerson().getName());
            System.out.println("Surname: " + ticket.getPerson().getSurname());
            System.out.println("Email: " + ticket.getPerson().getEmail());
            System.out.println();
            totalPrice += ticket.getPrice();
        }
        System.out.println("Total Price: £" + totalPrice);
        System.out.println(); // Add a gap after the process
    }



    // if user need to know about the information of any specific seat

    private static void search_ticket(Scanner scanner) {
        int row = check_input_validity(scanner, "row", 1, number_of_rows);
        int seat = check_input_validity(scanner, "seat", 1, seats_per_row);

        if (row == -1 || seat == -1) {
            return;
        }

        if (seats[row - 1][seat - 1] == 1) {
            Ticket ticket = seatTickets[row - 1][seat - 1];
            if (ticket != null) {
                ticket.print_ticket_info();
            }
        } else {
            System.out.println("This seat is available.");
        }
        System.out.println(); // Add a gap after the process
    }



    // divide and print the all ticket information from ascending order

    private static void sort_tickets() {
        for (int rowIdx = 0; rowIdx < ticketCount - 1; rowIdx++) {
            for (int seatIdx = 0; seatIdx < ticketCount - rowIdx - 1; seatIdx++) {
                if (tickets[seatIdx].getPrice() > tickets[seatIdx + 1].getPrice()) {
                    Ticket temp = tickets[seatIdx];
                    tickets[seatIdx] = tickets[seatIdx + 1];
                    tickets[seatIdx + 1] = temp;
                }
            }
        }

        System.out.println("Tickets ordered by price (ascending):");
        // information of ordered tickets from buyer by price (ascending)
        for (int i = 0; i < ticketCount; i++) {
            tickets[i].print_ticket_info();
            System.out.println();  // Adding a blank line between tickets for easy to read
        }
        System.out.println(); // Add a gap after the process
    }



    // Ask about buyer information

    private static Person get_person_details(Scanner scanner) {
        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter surname: ");
        String surname = scanner.next();
        System.out.print("Enter email: ");
        String email = scanner.next();
        return new Person(name, surname, email);
    }



    // calculate tickets prices by there rows where situated

    private static int calculate_price_by_row(int row) {
        switch (row) {
            case 1:
                return front_row_price;
            case 2:
                return middle_row_price;
            case 3:
                return back_row_price;
            default:
                return 0;
        }
    }
}
