package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {
    private final AtomicInteger nextId = new AtomicInteger();
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(
                0, "Intern Java Developer", "Разработка ПО", true, 1, 0));
        save(new Vacancy(
                0, "Junior Java Developer", "Разработка ПО", false, 2, 0));
        save(new Vacancy(
                0, "Junior+ Java Developer", "Разработка ПО", true, 3, 0));
        save(new Vacancy(
                0, "Middle Java Developer", "Разработка ПО", true, 1, 0));
        save(new Vacancy(
                0, "Middle+ Java Developer", "Разработка ПО", false, 2, 0));
        save(new Vacancy(
                0, "Senior Java Developer", "Разработка ПО", true, 3, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return vacancies.computeIfAbsent(nextId.incrementAndGet(), k -> {
            vacancy.setId(k);
            return vacancy;
        });
    }

    @Override
    public boolean deleteById(int id) {
        return Objects.nonNull(vacancies.remove(id));
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) ->
                new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(),
                        vacancy.getVisible(), vacancy.getCityId(), vacancy.getFileId())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}