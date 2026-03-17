#include <iostream>
using namespace std;

class OldPrinter {
public:
    void printOld() {
        cout << "Printing old format\n";
    }
};

class PrinterAdapter {
    OldPrinter printer;

public:
    void print() {
        printer.printOld();
    }
};

int main() {

    PrinterAdapter adapter;
    adapter.print();
}



/*
Legacy API
3rd party library
*/


