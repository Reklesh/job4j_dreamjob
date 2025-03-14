package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IndexControllerTest {

    @Test
    public void whenRequestIndexPageThenGetPage() {
        IndexController controller = new IndexController();
        var view = controller.getIndex();

        assertThat(view).isEqualTo("index");
    }
}