package JobPostingPortal.simpleone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JobMatchingService {

    private final Map<String, Job> jobs = new HashMap<>();
    private final Map<String, Candidate> candidates = new HashMap<>();

    void postJob(Job job) {
        jobs.put(job.id, job);
    }

    void registerCandidate(Candidate c) {
        candidates.put(c.id, c);
    }

    public Candidate getCandidate(String id) {
        return candidates.get(id);
    }


    //score
    private int score(Job job, Candidate c) {
        if (c.experience < job.minExperience) return 0;

        int match = 0;
        for (String skill : job.requiredSkills) {
            if (c.skills.contains(skill)) {
                match++;
            }
        }
        return match;
    }

    //  Recruiter View: Top Candidates
    /*
        topCandidates(jobId)
        Filtering: O(C × S)
        Sorting: O(C log C)

        Where:
        C = number of candidates
        S = number of skills
     */
    List<Candidate> topCandidates(String jobId, int k) {
        Job job = jobs.get(jobId);
        List<Candidate> list = new ArrayList<>();

        for (Candidate c : candidates.values()) {
            if (score(job, c) > 0) {
                list.add(c);
            }
        }

        list.sort((a, b) -> score(job, b) - score(job, a));

        if (list.size() > k) {
            return list.subList(0, k);
        }
        return list;
    }


    //Candidate View: Top Jobs
//    The current solution is intentionally  O(N log N).
/*
    topJobs(candidateId)
        Filtering: O(J × S)
        Sorting: O(J log J)

        Where:
        C = number of candidates
        J = number of jobs
        S = number of skills
 */
    List<Job> topJobs(String candidateId, int k) {
        Candidate c = candidates.get(candidateId);

        if (c == null) {
            throw new IllegalArgumentException(
                    "Candidate not found: " + candidateId
            );
        }

        List<Job> list = new ArrayList<>();

        for (Job j : jobs.values()) {
            if (score(j, c) > 0) {
                list.add(j);
            }
        }

        list.sort((a, b) -> score(b, c) - score(a, c));

        if (list.size() > k) {
            return list.subList(0, k);
        }
        return list;
    }
}

