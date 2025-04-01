import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import  java.sql.ResultSet;


public class hotel_Reservation_System {
     private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
     private static final String username = "root";
     private static final String password = "anantsingh922004";

    public static void main(String[] args) throws ClassNotFoundException ,SQLException{
       try {
           Class.forName("com.mysql.cj.jdbc.Driver");
       } catch ( ClassNotFoundException e) {
           System.out.println(e.getMessage());
       }

       try {
           Connection connection = DriverManager.getConnection(url,username,password);
           while(true){
               System.out.println();
               System.out.println("Hotel Management System");
               Scanner scanner = new Scanner(System.in);
               System.out.println("1. Reserve a room");
               System.out.println("2. View Reservation");
               System.out.println("3. Get Room Number");
               System.out.println("4. Update Reservations");
               System.out.println("5. Delete Reservations");
               System.out.println("0. Exit");
               System.out.print("Choose an Option: ");
               int choice = scanner.nextInt();
               switch (choice){
                   case 1:
                       reserveRoom(connection,scanner);
                       break;
                   case 2:
                       viewReservations(connection);
                       break;
                   case 3:
                       getRoomNumber(connection,scanner);
                       break;
                   case 4:
                       updateReservation(connection,scanner);
                       break;
                   case 5:
                       deleteReservation(connection,scanner);
                       break;
                   case 0:
                       exit();
                       scanner.close();
                       return;
                   default:
                       System.out.println("Invalid choice. Try again.");

               }
           }
       }catch(SQLException e) {
           System.out.println(e.getMessage());
       }catch (InterruptedException e){
          throw new RuntimeException();
       }
    }

    public static void reserveRoom(Connection connection , Scanner scanner){
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations(guest_name,room_number,contact_number)"+
                    "VALUES('"+ guestName +"',"+ roomNumber + ", '"+ contactNumber +"')";

            try(Statement statement = connection.createStatement()) {
               int affectedRows = statement.executeUpdate(sql);

               if (affectedRows > 0){
                   System.out.println("Reservation Successfully");
               }else{
                   System.out.println("Reservation Failed.");
               }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

   private static void viewReservations(Connection connection) throws SQLException{
        String sql = "SELECT reservation_id,guest_name, room_number ,contact_number,revervation_date FROM reservations";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);){
            System.out.println("Current Reservation:");
            System.out.println("+----------------+----------------+-----------------+----------------+---------------------------+");
            System.out.println("| Reservation ID | Guest          | Room Number     | Contact Number | Reservation Date          |");
            System.out.println("+----------------+----------------+-----------------+----------------+---------------------------+");

            while (resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guessName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("revervation_date").toString();

                // Format and Display the reservation data in a table-like format
                System.out.printf("| %-14d | %-14s | %-15d | %-14s | %-25s |\n",
                        reservationId,guessName,roomNumber,contactNumber,reservationDate);
            }
            System.out.println("+---------------+---------------+-------------------+---------------+----------------------------+");
        }
   }

    private static void getRoomNumber(Connection connection, Scanner scanner){
        try {
            System.out.println("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " + "WHERE reservation_id = " + reservationId + " AND guest_name = '" + guestName +"'";

            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
               if (resultSet.next()){
                   int roomNumber = resultSet.getInt("room_number");
                   System.out.println("Room Number for Reservation ID "+ reservationId + " and Guest "+ guestName + " is: "+ roomNumber);
               }else{
                   System.out.println("Reservation Not Found for the given Id and guest name.");
               }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();// Consume the New line Character

            if (!reservationExists(connection,reservationId)){
                System.out.println("Reservation Not found for Given Id.");
                return;
            }

            System.out.print("Enter new Guest name: ");
             String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "'," + "room_number = " + newRoomNumber + ", " +
             "contact_number = '" + newContactNumber + "'" + "WHERE reservation_id = "+ reservationId;

         try(Statement statement = connection.createStatement()) {
           int affectedRows = statement.executeUpdate(sql);

           if (affectedRows > 0){
               System.out.println("Reservation update Successfully!");
           } else {
               System.out.println("Reservation update failed.");
           }
         }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    private static void deleteReservation(Connection connection, Scanner scanner){
        try {
            System.out.print("Enter Reservation Id to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;
                                                                           
            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation Delete Successfully!");
                }else{
                    System.out.println("Reservation Deletion Failed ");
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

   private static boolean reservationExists(Connection connection, int reservationId){
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+ reservationId;

            
            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next();
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
   }

   private static void exit() throws InterruptedException{
       System.out.print("Exiting System");
       int i = 5;
       while (i != 0){
           System.out.print(" .");
           Thread.sleep(450);
           i--;
       }
       System.out.println();
       System.out.println("ThankYou For Using Hotel Reservation System!!!");
   }

}
