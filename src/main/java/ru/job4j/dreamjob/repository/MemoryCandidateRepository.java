package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan", "Intern Java Developer"));
        save(new Candidate(0, "Petr", "Junior Java Developer"));
        save(new Candidate(0, "Alex", "Junior+ Java Developer"));
        save(new Candidate(0, "Elena", "Middle Java Developer"));
        save(new Candidate(0, "Olga", "Middle+ Java Developer"));
        save(new Candidate(0, "Oleg", "Senior Java Developer"));
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidates.computeIfAbsent(nextId.incrementAndGet(), k -> {
            candidate.setId(k);
            return candidate;
        });
    }

    @Override
    public boolean deleteById(int id) {
        return Objects.nonNull(candidates.remove(id));
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
