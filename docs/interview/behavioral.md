# Interview Preparation: Behavioral Questions

## Q1: Tell me about a time you had to make a trade-off

### STAR Answer

**Situation:**
"We were building an AI-enhanced search feature with limited budget."

**Task:**
"We needed to decide between using expensive GPT-4 (95% accuracy) or cheaper GPT-3.5 (85% accuracy)."

**Action:**
"I analyzed the use case:
- For e-commerce, 85% accuracy is good enough
- Cost difference: 30x ($0.03 vs $0.001 per request)
- At 100K requests/month, difference was $3K vs $100
- ROI was only 10% improvement in quality"

"I recommended GPT-3.5 with the option to upgrade specific queries to GPT-4 if needed."

**Result:**
"Saved $33K/year while maintaining 85% solution quality. Team approved the approach."

---

## Q2: Describe a problem you solved

### STAR Answer

**Situation:**
"Our AI-enhanced recommendations were expensive: $0.02 per user per session."

**Task:**
"We needed to reduce costs without degrading results."

**Action:**
"I implemented multi-level caching:
1. Full response caching (30% hit rate)
2. Embedding-based deduplication (additional 25% hit rate)
3. Background async processing"

"Result: 55% of requests served from cache in <10ms."

**Result:**
"Reduced cost to $0.009 per user (-55%). Improved latency. User satisfaction increased."

---

## Q3: How do you handle disagreement with a team member?

### Good Answer

"I believe in data-driven decisions. If someone disagreed with my caching strategy, I would:
1. Listen to their concerns
2. Gather metrics/data to compare approaches
3. Run a small experiment if needed
4. Present findings to team
5. Implement best solution regardless of who proposed it"

---

## Q4: What's your approach to learning?

### Good Answer

"I'm always learning, especially with rapidly-changing AI space:
- Read papers on LLM optimization
- Experiment with new techniques in side projects
- Share learnings with team
- Practice system design questions
- Document solutions for team reference"

---

## Q5: Why do you want this role?

### Good Answer

"I'm excited about AI integration in traditional systems. Your team is doing that well:
- Good architecture (service layer AI)
- Focus on observability and costs
- Real-world scale problems

I want to:
- Deepen expertise in AI systems
- Work on multi-provider optimization
- Help build scalable AI features
- Learn from experienced team"

---
