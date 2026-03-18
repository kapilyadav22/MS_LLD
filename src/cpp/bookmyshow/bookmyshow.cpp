#include <bits/stdc++.h>
using namespace std;

enum class SeatStatus { AVAILABLE, LOCKED, BOOKED };

class Seat {
    int id;
    SeatStatus status;

public:
    Seat(int id) : id(id), status(SeatStatus::AVAILABLE) {}

    bool available() const { return status == SeatStatus::AVAILABLE; }

    void lock() { status = SeatStatus::LOCKED; }

    void book() { status = SeatStatus::BOOKED; }

    string getStatus() const {
        if(status == SeatStatus::AVAILABLE) return "Available";
        if(status == SeatStatus::LOCKED) return "Locked";
        return "Booked";
    }
};

class Movie {
public:
    string name;

    Movie() {}
    Movie(string n) : name(n) {}
};

class Show {
    int id;
    Movie movie;
    vector<Seat> seats;
    mutex mtx;

public:
    Show(int id, const Movie& movie, int seatCount)
        : id(id), movie(movie) {

        for(int i=1;i<=seatCount;i++)
            seats.emplace_back(i);
    }

    bool lockSeats(const vector<int>& seatNums) {
        lock_guard<mutex> lock(mtx);

        for(int s : seatNums)
            if(!seats[s-1].available())
                return false;

        for(int s : seatNums)
            seats[s-1].lock();

        return true;
    }

    void confirmSeats(const vector<int>& seatNums) {
        lock_guard<mutex> lock(mtx);

        for(int s : seatNums)
            seats[s-1].book();
    }

    void printSeats() const {
        for(const auto &seat : seats)
            cout << seat.getStatus() << " ";
        cout << "\n";
    }
};

class PaymentStrategy {
public:
    virtual bool pay(int amount) = 0;
    virtual ~PaymentStrategy() = default;
};

class CardPayment : public PaymentStrategy {
public:
    bool pay(int amount) override {
        cout << "Payment via CARD " << amount << "\n";
        return true;
    }
};

class UpiPayment : public PaymentStrategy {
public:
    bool pay(int amount) override {
        cout << "Payment via UPI " << amount << "\n";
        return true;
    }
};

class PaymentFactory {
public:
    static unique_ptr<PaymentStrategy> createPayment(const string& type) {
        if(type == "CARD") return make_unique<CardPayment>();
        if(type == "UPI") return make_unique<UpiPayment>();
        return nullptr;
    }
};

class BookingService {

    unordered_map<string, Movie> movies;
    unordered_map<int, Show> shows;

public:
    int calculateFare(const vector<int>& seats) {
        return seats.size() * 100;
    }
    void addMovie(const string& name) {
        movies.emplace(name, Movie(name));
        cout << "Movie Added\n";
    }

    void addShow(int showId, const string& movieName, int seatCount) {
        if(!movies.count(movieName))
            return void(cout<<"Movie Not Found\n");
        if(shows.count(showId))
            return void(cout<<"Show Already Exists\n");
        shows.try_emplace(showId, showId, movies[movieName], seatCount);
        cout << "Show Added\n";
    }

    void showSeats(int showId) {
        auto it = shows.find(showId);
        if(it == shows.end())
            return void(cout<<"Invalid Show\n");
        it->second.printSeats();
    }

    void book(int showId, int bookingId, vector<int> seatNums, const string& paymentType) {
        auto it = shows.find(showId);
        if(it == shows.end())
            return void(cout<<"Invalid Show\n");

        auto payment = PaymentFactory::createPayment(paymentType);
        if(!payment)
            return void(cout<<"Invalid Payment Type\n");

        auto &show = it->second;

        if(!show.lockSeats(seatNums))
            return void(cout<<"Seats Unavailable\n");

        int amount = calculateFare(seatNums);

        if(payment->pay(amount)) {
            show.confirmSeats(seatNums);
            cout<<"Booking Successful "<<bookingId<<" amount : "<<amount<<endl;
        }
    }
};

int main() {
    BookingService service;
    string command;

    while(cin >> command) {

        if(command == "ADD_MOVIE") {
            string movie; cin >> movie;
            service.addMovie(movie);
        }

        else if(command == "ADD_SHOW") {
            int showId, seats;
            string movie;
            cin >> showId >> movie >> seats;
            service.addShow(showId, movie, seats);
        }

        else if(command == "SHOW_SEATS") {
            int showId; cin >> showId;
            service.showSeats(showId);
        }

        else if(command == "BOOK") {
            int showId, bookingId;
            cin >> showId >> bookingId;

            vector<int> seats;
            string token;

            while(cin >> token) {
                if(token == "CARD" || token == "UPI") {
                    service.book(showId, bookingId, seats, token);
                    break;
                }
                seats.push_back(stoi(token));
            }
        }

        else if(command == "EXIT")
            break;
    }
}
/*

Supported Commands:

    ADD_MOVIE <movie>
    ADD_SHOW <showId> <movie> <seatCount>
    SHOW_SEATS <showId>
    BOOK <showId> <bookingId> <seat1 seat2 ...> <PAYMENT>

INPUT:
    ADD_MOVIE Avengers
    ADD_SHOW 1 Avengers 5
    SHOW_SEATS 1
    BOOK 1 101 1 2 CARD
    SHOW_SEATS 1
    BOOK 1 102 2 3 UPI
    SHOW_SEATS 1
    EXIT

OUTPUT:
    Movie Added
    Show Added
    Available Available Available Available Available
    Payment via CARD 500
    Booking Successful 101
    Booked Booked Available Available Available
    Seats Unavailable
    Booked Booked Available Available Available
*/