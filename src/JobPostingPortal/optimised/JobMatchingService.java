package JobPostingPortal.optimised;

import JobPostingPortal.simpleone.Candidate;
import JobPostingPortal.simpleone.Job;

import java.util.*;


public class JobMatchingService {

    private final Map<String, Job> jobs = new HashMap<>();
    private final Map<String, Candidate> candidates = new HashMap<>();

    // ------------------- Public APIs -------------------

    void postJob(Job job) {
        jobs.put(job.getId(), job);
    }

    void registerCandidate(Candidate candidate) {
        candidates.put(candidate.getId(), candidate);
    }

    // Recruiter flow
    List<Candidate> topCandidates(String jobId, int k) {
        Job job = jobs.get(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }

        PriorityQueue<ScoredCandidate> minHeap = new PriorityQueue<>((a, b) -> a.score - b.score);

        for (Candidate c : candidates.values()) {
            int s = score(job, c);
            if (s <= 0) continue;

            minHeap.offer(new ScoredCandidate(c, s));
            if (minHeap.size() > k) {
                minHeap.poll(); // remove lowest score
            }
        }

        return extractTopCandidates(minHeap);
    }

    // Candidate flow
    List<Job> topJobs(String candidateId, int k) {
        Candidate c = candidates.get(candidateId);
        if (c == null) {
            throw new IllegalArgumentException("Candidate not found: " + candidateId);
        }

        PriorityQueue<ScoredJob> minHeap =
                new PriorityQueue<>((a, b) -> a.score - b.score);

        for (Job j : jobs.values()) {
            int s = score(j, c);
            if (s <= 0) continue;

            minHeap.offer(new ScoredJob(j, s));
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return extractTopJobs(minHeap);
    }

    // ------------------- Core Matching Logic -------------------

    private int score(Job job, Candidate c) {
        if (c.getExperience() < job.getminExperience()) return 0;

        int match = 0;
        for (String skill : job.getSkills()) {
            if (c.getSkills().contains(skill)) {
                match++;
            }
        }
        return match;
    }

    // ------------------- Helpers -------------------

    private List<Candidate> extractTopCandidates(
            PriorityQueue<ScoredCandidate> heap) {

        List<Candidate> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.poll().candidate);
        }
        Collections.reverse(result); // highest score first
        return result;
    }

    private List<Job> extractTopJobs(
            PriorityQueue<ScoredJob> heap) {

        List<Job> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.poll().job);
        }
        Collections.reverse(result);
        return result;
    }

    // ------------------- Internal DTOs -------------------

    private static class ScoredCandidate {
        Candidate candidate;
        int score;

        ScoredCandidate(Candidate candidate, int score) {
            this.candidate = candidate;
            this.score = score;
        }
    }

    private static class ScoredJob {
        Job job;
        int score;

        ScoredJob(Job job, int score) {
            this.job = job;
            this.score = score;
        }
    }
}
