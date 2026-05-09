package bookmyshow;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

enum SeatStatus {
    AVAILABLE, LOCKED, BOOKED
}

enum BookingStatus {
    PENDING, CONFIRMED, FAILED
}

enum PaymentType {
    CARD, UPI
}

class Seat {
    private int seatNumber;
    private SeatStatus status;
    private ReentrantLock lock;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.status = SeatStatus.AVAILABLE;
        this.lock = new ReentrantLock();
    }

    public boolean lockSeat() {
        lock.lock();
        try {
            if (status != SeatStatus.AVAILABLE) {
                return false;
            }
            status = SeatStatus.LOCKED;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void unlockSeat() {
        lock.lock();
        try {
            status = SeatStatus.AVAILABLE;
        } finally {
            lock.unlock();
        }
    }

    public void bookSeat() {
        lock.lock();
        try {
            status = SeatStatus.BOOKED;
        } finally {
            lock.unlock();
        }
    }

    public String getStatus() {
        if (status == SeatStatus.AVAILABLE) {
            return "Available";
        }
        if (status == SeatStatus.LOCKED) {
            return "Locked";
        }
        return "Booked";
    }
}

class Movie {
    String name;

    public Movie(String name) {
        this.name = name;
    }
}

class User {
    private int userId;
    private String name;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}

class Show {
    private int id;
    private Movie movie;
    private List<Seat> seats;

    public Show(int id, Movie movie, int seatCount) {
        this.id = id;
        this.movie = movie;
        this.seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            seats.add(new Seat(i));
        }
    }

    public boolean lockSeats(List<Integer> seatNums) {
        List<Seat> lockedSeats = new ArrayList<>();
        for (int s : seatNums) {
            if (s <= 0 || s > seats.size()) {
                unlockSeatsInternal(lockedSeats);
                return false;
            }

            Seat seat = seats.get(s - 1);
            if (!seat.lockSeat()) {
                unlockSeatsInternal(lockedSeats);
                return false;
            }
            lockedSeats.add(seat);
        }
        return true;
    }

    private void unlockSeatsInternal(List<Seat> lockedSeats) {
        for (Seat seat : lockedSeats) {
            seat.unlockSeat();
        }
    }

    public void unlockSeats(List<Integer> seatNums) {
        for (int s : seatNums) {
            seats.get(s - 1).unlockSeat();
        }
    }

    public void confirmSeats(List<Integer> seatNums) {
        for (int s : seatNums) {
            seats.get(s - 1).bookSeat();
        }
    }

    public List<Seat> getSeats(List<Integer> seatNums) {
        List<Seat> bookedSeats = new ArrayList<>();
        for (int s : seatNums) {
            bookedSeats.add(seats.get(s - 1));
        }
        return bookedSeats;
    }

    public void printSeats() {
        for (Seat seat : seats) {
            System.out.print(seat.getStatus() + " ");
        }
        System.out.println();
    }
}

class Booking {
    private int bookingId;
    private User user;
    private Show show;
    private List<Seat> seats;
    private BookingStatus status;

    public Booking(int bookingId, User user, Show show, List<Seat> seats) {
        this.bookingId = bookingId;
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.status = BookingStatus.PENDING;
    }

    public void confirm() {
        status = BookingStatus.CONFIRMED;
    }

    public void fail() {
        status = BookingStatus.FAILED;
    }

    public BookingStatus getStatus() {
        return status;
    }
}

interface PaymentStrategy {
    boolean pay(int amount);
}

class CardPayment implements PaymentStrategy {
    public boolean pay(int amount) {
        System.out.println("Payment via CARD: " + amount);
        return true;
    }
}

class UpiPayment implements PaymentStrategy {
    public boolean pay(int amount) {
        System.out.println("Payment via UPI: " + amount);
        return true;
    }
}

class PaymentFactory {
    public static PaymentStrategy createPayment(PaymentType type) {
        switch (type) {
            case CARD:
                return new CardPayment();
            case UPI:
                return new UpiPayment();
            default:
                return null;
        }
    }
}

class BookingService {
    private Map<String, Movie> movies = new HashMap<>();
    private Map<Integer, Show> shows = new HashMap<>();
    private Map<Integer, Booking> bookings = new HashMap<>();

    public void addMovie(String name) {
        movies.putIfAbsent(name, new Movie(name));
        System.out.println("Movie Added");
    }

    public void addShow(int showId, String movieName, int seatCount) {
        if (!movies.containsKey(movieName)) {
            System.out.println("Movie Not Found");
            return;
        }
        if (shows.containsKey(showId)) {
            System.out.println("Show Already Exists");
            return;
        }

        shows.put(showId, new Show(showId, movies.get(movieName), seatCount));
        System.out.println("Show Added");
    }

    public void showSeats(int showId) {
        Show show = shows.get(showId);
        if (show == null) {
            System.out.println("Invalid Show");
            return;
        }
        show.printSeats();
    }

    private int calculateFare(List<Integer> seats) {
        return seats.size() * 100;
    }

    public void book(User user, int showId, int bookingId, List<Integer> seatNums, PaymentType paymentType) {
        Show show = shows.get(showId);
        if (show == null) {
            System.out.println("Invalid Show");
            return;
        }

        PaymentStrategy payment = PaymentFactory.createPayment(paymentType);
        if (payment == null) {
            System.out.println("Invalid Payment Type");
            return;
        }

        if (!show.lockSeats(seatNums)) {
            System.out.println("Seats Unavailable");
            return;
        }

        Booking booking = new Booking(bookingId, user, show, show.getSeats(seatNums));
        bookings.put(bookingId, booking);
        int amount = calculateFare(seatNums);

        if (payment.pay(amount)) {
            show.confirmSeats(seatNums);
            booking.confirm();
            System.out.println("Booking Successful: " + bookingId);
        } else {
            show.unlockSeats(seatNums);
            booking.fail();
            System.out.println("Payment Failed");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BookingService service = new BookingService();
        service.addMovie("Avengers");
        service.addShow(1, "Avengers", 5);

        User user1 = new User(1, "Kapil");
        service.showSeats(1);

        service.book(user1, 1, 101, Arrays.asList(1, 2), PaymentType.CARD);
        service.showSeats(1);

        service.book(user1, 1, 102, Arrays.asList(2, 3), PaymentType.UPI);
        service.showSeats(1);
    }
}

/*
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BookingService service = new BookingService();

        while (sc.hasNext()) {
            String command = sc.next();

            if (command.equals("ADD_MOVIE")) {
                String movie = sc.next();
                service.addMovie(movie);
            } else if (command.equals("ADD_SHOW")) {
                int showId = sc.nextInt();
                String movie = sc.next();
                int seats = sc.nextInt();
                service.addShow(showId, movie, seats);
            } else if (command.equals("SHOW_SEATS")) {
                int showId = sc.nextInt();
                service.showSeats(showId);
            } else if (command.equals("BOOK")) {
                int showId = sc.nextInt();
                int bookingId = sc.nextInt();
                List<Integer> seats = new ArrayList<>();

                while (sc.hasNext()) {
                    String token = sc.next();
                    if (token.equals("CARD") || token.equals("UPI")) {
                        service.book(showId, bookingId, seats, token);
                        break;
                    }
                    seats.add(Integer.parseInt(token));
                }
            } else if (command.equals("EXIT")) {
                break;
            }
        }

        sc.close();
    }
}
*/
