#include <bits/stdc++.h>
using namespace std;

class PaymentGateway {
    public:
    virtual void sendMoney(int money)=0;
    virtual ~PaymentGateway(){};

};

class JustPay : public PaymentGateway {
    public:
    void sendMoney(int money) override {
        cout<<"Payment sent via JustPay of Rs. "<<money<<endl;
    }

};


class PhonePay : public PaymentGateway {
    public:
    void sendMoney(int money) override {
        cout<<"Payment sent via PhonePay of Rs. "<<money<<endl;
    }
};

class Paytm : public PaymentGateway {
    public:
    void sendMoney(int money) override {
        cout<<"Payment sent via Paytm of Rs. "<<money<<endl;
    }
};


class PaymentGatewayFactory {
    public:
    static shared_ptr<PaymentGateway> create(string paymentGatewayType){
        if(paymentGatewayType=="PAYTM"){
            return make_shared<Paytm>();
        } else  if(paymentGatewayType=="PHONEPE"){
            return make_shared<PhonePay>();
        }  else if(paymentGatewayType=="JUSTPAY"){
            return make_shared<JustPay>();
        }
        return nullptr;
    }
};

class PaymentManager {

   shared_ptr<PaymentGateway> paymentGateway;
    public:
    void  init(string paymentGatewayType){
        paymentGateway = PaymentGatewayFactory::create(paymentGatewayType);
    }

    void pay(int amount){
         if(!paymentGateway) {
             cout<<"paymentGateway is not initialized";
             return;
         }
            // throw runtime_error("Gateway not initialized");
        paymentGateway->sendMoney(amount);
    }

    // unique_ptr<PaymentGateway> paymentGateway = make_unique<Paytm>();
    // paymentGateway->sendMoney(500);

};

int main() {
    PaymentManager paymentManager;
    string command;
    while(cin>>command){
        if(command=="EXIT"){
            cout<<"exiting"<<endl;
        } else {
            int amount;
            cin>>amount;
            paymentManager.init(command);
            paymentManager.pay(amount);
        }

    }



}
