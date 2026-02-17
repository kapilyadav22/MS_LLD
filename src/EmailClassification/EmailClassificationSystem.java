package EmailClassification;

import java.util.*;

/*
 * Entry point (can be used for demo / testing)
 */
public class EmailClassificationSystem {

    public static void main(String[] args) {

        Email email = new Email(
                "1",
                "offers@amazon.com",
                List.of("user@outlook.com"),
                "Big Sale Today",
                "Huge discount on electronics"
        );

        List<Rule> rules = List.of(
                new PromotionKeywordRule(),
                new SpamSenderRule()
        );

        EmailClassifier classifier =
                new HybridEmailClassifier(
                        new RuleBasedClassifier(rules),
                        new MLBasedClassifier()
                );

        EmailClassificationService service =
                new EmailClassificationService(classifier);

        Category category = service.classifyEmail(email);
        System.out.println("Email classified as: " + category);
    }
}

/* ======================= DOMAIN ======================= */

class Email {
    String id;
    String from;
    List<String> to;
    String subject;
    String body;
    Map<String, String> headers = new HashMap<>();

    Email(String id, String from, List<String> to, String subject, String body) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}

enum Category {
    PRIMARY,
    PROMOTIONS,
    SOCIAL,
    UPDATES,
    SPAM
}

/* =================== STRATEGY =================== */

interface EmailClassifier {
    Category classify(Email email);
}

/* ================= RULE-BASED ================= */

class RuleBasedClassifier implements EmailClassifier {

    private final List<Rule> rules;

    RuleBasedClassifier(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Category classify(Email email) {
        for (Rule rule : rules) {
            if (rule.matches(email)) {
                return rule.getCategory();
            }
        }
        return Category.PRIMARY;
    }
}

/* ================== ML-BASED ================== */

class MLBasedClassifier implements EmailClassifier {

    private final MLModel model = new MLModel();

    @Override
    public Category classify(Email email) {
        return model.predict(email);
    }
}

/* =================== HYBRID =================== */

class HybridEmailClassifier implements EmailClassifier {

    private final EmailClassifier ruleClassifier;
    private final EmailClassifier mlClassifier;

    HybridEmailClassifier(EmailClassifier ruleClassifier,
                          EmailClassifier mlClassifier) {
        this.ruleClassifier = ruleClassifier;
        this.mlClassifier = mlClassifier;
    }

    @Override
    public Category classify(Email email) {
        Category ruleResult = ruleClassifier.classify(email);
        if (ruleResult != Category.PRIMARY) {
            return ruleResult;
        }
        return mlClassifier.classify(email);
    }
}

/* ================== RULES ================== */

interface Rule {
    boolean matches(Email email);
    Category getCategory();
}

class PromotionKeywordRule implements Rule {

    @Override
    public boolean matches(Email email) {
        return email.subject.toLowerCase().contains("sale")
                || email.body.toLowerCase().contains("discount");
    }

    @Override
    public Category getCategory() {
        return Category.PROMOTIONS;
    }
}

class SpamSenderRule implements Rule {

    private static final Set<String> BLACKLIST =
            Set.of("spam@bad.com", "fraud@scam.com");

    @Override
    public boolean matches(Email email) {
        return BLACKLIST.contains(email.from);
    }

    @Override
    public Category getCategory() {
        return Category.SPAM;
    }
}

/* ================= SERVICE ================= */

class EmailClassificationService {

    private final EmailClassifier classifier;

    EmailClassificationService(EmailClassifier classifier) {
        this.classifier = classifier;
    }

    public Category classifyEmail(Email email) {
        return classifier.classify(email);
    }
}

/* ================= ML PLACEHOLDER ================= */

class MLModel {

    Category predict(Email email) {
        // Simulated ML prediction
        if (email.from.contains("linkedin")) {
            return Category.SOCIAL;
        }
        return Category.PRIMARY;
    }
}
