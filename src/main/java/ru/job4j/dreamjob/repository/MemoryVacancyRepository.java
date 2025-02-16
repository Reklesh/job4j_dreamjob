package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.util.*;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();
    private int nextId = 1;
    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Разработка ПО"));
        save(new Vacancy(0, "Junior Java Developer", "Разработка ПО"));
        save(new Vacancy(0, "Junior+ Java Developer", "Разработка ПО"));
        save(new Vacancy(0, "Middle Java Developer", "Разработка ПО"));
        save(new Vacancy(0, "Middle+ Java Developer", "Разработка ПО"));
        save(new Vacancy(0, "Senior Java Developer", "Разработка ПО"));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return Objects.nonNull(vacancies.remove(id));
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) ->
                new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription())) != null;
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