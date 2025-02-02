package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private int nextId = 1;
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan", "Intern Java Developer"));
        save(new Candidate(0, "Petr", "Junior Java Developer"));
        save(new Candidate(0, "Alex", "Junior+ Java Developer"));
        save(new Candidate(0, "Elena", "Middle Java Developer"));
        save(new Candidate(0, "Olga", "Middle+ Java Developer"));
        save(new Candidate(0, "Oleg", "Senior Java Developer"));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return Objects.nonNull(candidates.replace(candidate.getId(), candidate));
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
