package JobPostingPortal.simpleone;

import java.util.Set;

public class Candidate {
    final String id;
    final String name;
    final int experience;
    final Set<String> skills;

    Candidate(String id, String name, int yoe, Set<String> skills) {
        this.id = id;
        this.name = name;
        this.experience = yoe;
        this.skills = skills;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getExperience() {
        return experience;
    }
    public Set<String> getSkills() {
        return skills;
    }

}
