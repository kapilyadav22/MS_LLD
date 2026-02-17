Job Posting Service – LLD (Microsoft SDE-2)

1. Requirements (Clarified in Interview Style)

Functional
    Recruiter can post a job
    Candidate can apply to a job
    Maintain candidate profile (skills, YOE)
    Recruiter sees top matching candidates for a job
    Candidate sees top eligible jobs
    Matching logic is the core

Non-Functional (Lightweight)
    In-memory (no DB)
    Read-heavy
    Deterministic ranking
    Easy to extend (weights, filters)

2. Core Design Decisions (Checklist Friendly)

   | Area      | Decision            | Why MS Likes It      |
   | --------- | ------------------- | -------------------- |
   | Pattern   | Strategy (Matching) | Extensible scoring   |
   | Storage   | In-memory Maps      | Interview-friendly   |
   | Ranking   | Score-based         | Clear logic          |
   | Threading | Single-thread       | Explicitly stated    |
   | APIs      | Service layer       | Clean separation     |
   | Code      | Fully runnable      | Explicit requirement |

3.Data Models

Candidate
Job


4. Matching Strategy (Most Important)

Scoring Rules (Explain Verbally)
  -  Skill match → 70% weight
  - Experience match → 30% weight
  - Candidate must satisfy minimum experience
