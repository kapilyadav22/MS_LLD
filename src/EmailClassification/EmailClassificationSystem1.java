import java.util.*;

/*
 * ======================= DEMO =======================
 */
public class EmailClassificationSystem1 {

    public static void main(String[] args) {

        Email1 email = new Email1(
                "1",
                "offers@amazon.com",
                List.of("user@outlook.com"),
                "Big Sale Today",
                "Huge discount on electronics"
        );

        List<Rule1> rules = List.of(
                new SpamSenderRule1(),
                new PromotionKeywordRule1()
        );

        EmailClassifier1 classifier =
                new HybridEmailClassifier1(
                        new RuleBasedClassifier1(rules),
                        new MLBasedClassifier1()
                );

        EmailClassificationService1 service =
                new EmailClassificationService1(classifier);

        ClassificationResult1 result = service.classifyEmail(email);

        System.out.println("Categories : " + result.categories);
        System.out.println("Reason     : " + result.reason);
    }
}

/* ======================= DOMAIN ======================= */

class Email1 {
    final String id;
    final String from;
    final List<String> to;
    final String subject;
    final String body;

    Email1(String id, String from, List<String> to, String subject, String body) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}

enum Category1 {
    PRIMARY,
    PROMOTIONS,
    SOCIAL,
    UPDATES,
    SPAM
}

/* ======================= RESULT ======================= */

class ClassificationResult1 {
    final Set<Category1> categories;
    final String reason;

    ClassificationResult1(Set<Category1> categories, String reason) {
        this.categories = categories;
        this.reason = reason;
    }
}

/* ======================= STRATEGY ======================= */

interface EmailClassifier1 {
    ClassificationResult1 classify(Email1 email);
}

/* ======================= RULE-BASED ======================= */

class RuleBasedClassifier1 implements EmailClassifier1 {

    private final List<Rule1> rules;

    RuleBasedClassifier1(List<Rule1> rules) {
        this.rules = new ArrayList<>(rules);
        this.rules.sort(Comparator.comparingInt(Rule1::priority));
    }

    @Override
    public ClassificationResult1 classify(Email1 email) {

        Set<Category1> categories = new HashSet<>();
        StringBuilder reason = new StringBuilder();

        for (Rule1 rule : rules) {
            if (rule.matches(email)) {
                categories.add(rule.getCategory());
                reason.append(rule.reason()).append("; ");
            }
        }

        if (categories.isEmpty()) {
            categories.add(Category1.PRIMARY);
            reason.append("No rule matched");
        }

        return new ClassificationResult1(categories, reason.toString());
    }
}

/* ======================= ML-BASED ======================= */

class MLBasedClassifier1 implements EmailClassifier1 {

    private final MLModel1 model = new MLModel1();

    @Override
    public ClassificationResult1 classify(Email1 email) {

        Prediction1 prediction = model.predict(email);

        if (prediction.confidence < 0.7) {
            return new ClassificationResult1(
                    Set.of(Category1.PRIMARY),
                    "ML confidence too low"
            );
        }

        return new ClassificationResult1(
                Set.of(prediction.category),
                "ML predicted with confidence " + prediction.confidence
        );
    }
}

/* ======================= HYBRID ======================= */

class HybridEmailClassifier1 implements EmailClassifier1 {

    private final EmailClassifier1 ruleClassifier;
    private final EmailClassifier1 mlClassifier;

    HybridEmailClassifier1(EmailClassifier1 ruleClassifier,
                           EmailClassifier1 mlClassifier) {
        this.ruleClassifier = ruleClassifier;
        this.mlClassifier = mlClassifier;
    }

    @Override
    public ClassificationResult1 classify(Email1 email) {

        ClassificationResult1 ruleResult = ruleClassifier.classify(email);

        if (!(ruleResult.categories.size() == 1 &&
                ruleResult.categories.contains(Category1.PRIMARY))) {
            return ruleResult;
        }

        return mlClassifier.classify(email);
    }
}

/* ======================= RULES ======================= */

interface Rule1 {
    boolean matches(Email1 email);
    Category1 getCategory();
    int priority();
    String reason();
}

class PromotionKeywordRule1 implements Rule1 {

    @Override
    public boolean matches(Email1 email) {
        return email.subject.toLowerCase().contains("sale")
                || email.body.toLowerCase().contains("discount");
    }

    @Override
    public Category1 getCategory() {
        return Category1.PROMOTIONS;
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public String reason() {
        return "Matched promotion keyword";
    }
}

class SpamSenderRule1 implements Rule1 {

    private static final Set<String> BLACKLIST =
            Set.of("spam@bad.com", "fraud@scam.com");

    @Override
    public boolean matches(Email1 email) {
        return BLACKLIST.contains(email.from);
    }

    @Override
    public Category1 getCategory() {
        return Category1.SPAM;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public String reason() {
        return "Sender found in blacklist";
    }
}

/* ======================= SERVICE ======================= */

class EmailClassificationService1 {

    private final EmailClassifier1 classifier;

    EmailClassificationService1(EmailClassifier1 classifier) {
        this.classifier = classifier;
    }

    public ClassificationResult1 classifyEmail(Email1 email) {
        return classifier.classify(email);
    }
}

/* ======================= ML PLACEHOLDER ======================= */

class Prediction1 {
    final Category1 category;
    final double confidence;

    Prediction1(Category1 category, double confidence) {
        this.category = category;
        this.confidence = confidence;
    }
}

class MLModel1 {

    Prediction1 predict(Email1 email) {
        if (email.from.contains("linkedin")) {
            return new Prediction1(Category1.SOCIAL, 0.9);
        }
        return new Prediction1(Category1.PRIMARY, 0.6);
    }
}
