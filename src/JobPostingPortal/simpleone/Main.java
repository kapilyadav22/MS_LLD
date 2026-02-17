package JobPostingPortal.simpleone;

import java.util.Set;

public class Main {
    public static void main(String[] args) {

        JobMatchingService service = new JobMatchingService();

        service.postJob(new Job( "J1","Software engineer", 1, Set.of("Java", "SQL")));

        service.registerCandidate(new Candidate( "C1","Kapil Yadav", 4, Set.of("Java", "SQL", "Docker") ));
        service.registerCandidate(new Candidate("C2","Rahul Chauhan", 3, Set.of("Java")));

        System.out.println("Top candidates:");

        //Recruiter flow
        String jobId = "J1";
        for (Candidate c : service.topCandidates(jobId, 5)) {
            System.out.println("Top Candidate  for " + jobId  + " is " +  c.id);
        }

        // Candidate flow
        String candidateId = "C1";
        System.out.println("Top jobs:");
        for (Job j : service.topJobs("C1", 5)) {
            System.out.println(j.id);
            System.out.println("Top Candidate  for " + candidateId  + " is " +  j.id);
        }
    }
}

