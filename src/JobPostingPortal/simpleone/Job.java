package JobPostingPortal.simpleone;

import java.util.Set;

public class Job {
    final String id;
    final String title;
    final int minExperience;
    final Set<String> requiredSkills;

    Job(String id, String title, int minExp, Set<String> skills) {
        this.id = id;
        this.title = title;
        this.minExperience = minExp;
        this.requiredSkills = skills;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public int getminExperience() {
        return minExperience;
    }
    public Set<String> getSkills() {
        return requiredSkills;
    }

}

