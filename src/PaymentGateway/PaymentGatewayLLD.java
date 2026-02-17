package PaymentGateway;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

enum PaymentState {
    INITIATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED
}

enum PaymentMethod {
    CARD, UPI
}

class Payment {
    String id;
    double amount;
    PaymentMethod method;
    PaymentState state;

    Payment(String id, double amount, PaymentMethod method) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.state = PaymentState.INITIATED;
    }
}

interface PaymentProcessor {
    boolean process(Payment payment);
}

class CardProcessor implements PaymentProcessor {
    public boolean process(Payment payment) {
        System.out.println("Processing card payment: " + payment.id);
        return true; // simulate success
    }
}

class UpiProcessor implements PaymentProcessor {
    public boolean process(Payment payment) {
        System.out.println("Processing UPI payment: " + payment.id);
        return true;
    }
}

class PaymentService {
    private Map<String, Payment> payments = new ConcurrentHashMap<>();
    private Set<String> idempotencyKeys = ConcurrentHashMap.newKeySet();

    // Valid state transitions
    private static final Map<PaymentState, Set<PaymentState>> TRANSITIONS = Map.of(
            PaymentState.INITIATED, Set.of(PaymentState.AUTHORIZED, PaymentState.FAILED),
            PaymentState.AUTHORIZED, Set.of(PaymentState.CAPTURED, PaymentState.FAILED),
            PaymentState.CAPTURED, Set.of(PaymentState.REFUNDED)
    );

    public Payment createPayment(String id, double amount, PaymentMethod method, String idempotencyKey) {
        // Handle idempotency
        if (!idempotencyKeys.add(idempotencyKey)) {
            return payments.get(id);
        }

        Payment payment = new Payment(id, amount, method);
        payments.put(id, payment);
        return payment;
    }

    public boolean authorize(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) return false;

        return transition(payment, PaymentState.AUTHORIZED, getProcessor(payment.method));
    }

    public boolean capture(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) return false;

        return transition(payment, PaymentState.CAPTURED, getProcessor(payment.method));
    }

    public boolean refund(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) return false;

        return transition(payment, PaymentState.REFUNDED, p -> true);
    }

    private boolean transition(Payment payment, PaymentState newState, PaymentProcessor processor) {
        // Validate transition
        Set<PaymentState> validStates = TRANSITIONS.getOrDefault(payment.state, Set.of());
        if (!validStates.contains(newState)) {
            throw new IllegalStateException("Invalid transition: " + payment.state + " -> " + newState);
        }

        // Process payment
        if (processor.process(payment)) {
            payment.state = newState;
            return true;
        }

        payment.state = PaymentState.FAILED;
        return false;
    }

    private PaymentProcessor getProcessor(PaymentMethod method) {
        return method == PaymentMethod.CARD ? new CardProcessor() : new UpiProcessor();
    }
}

public class PaymentGatewayLLD {
    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        // Create payment
        Payment payment = service.createPayment("PAY123", 500.0, PaymentMethod.CARD, "KEY1");
        System.out.println("Created: " + payment.state);

        // Process payment flow
        service.authorize("PAY123");
        System.out.println("After authorize: " + payment.state);

        service.capture("PAY123");
        System.out.println("After capture: " + payment.state);
    }
}