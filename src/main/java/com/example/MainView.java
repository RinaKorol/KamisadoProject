package com.example;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;

@Route("")
@PageTitle("Kamisado")
public class MainView extends VerticalLayout {
    private Select<String> algorithmSelect;
    private Select<String> difficultySelect;
    private Button playButton;
    public MainView() {
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        H2 heading = new H2("Камисадо");
        heading.getStyle().set("font-family", "cursive")
                .set("fontSize", "30px");

        // Выпадающий список для выбора алгоритма
        algorithmSelect = new Select<>();
        algorithmSelect.setLabel("Выберите алгоритм");
        algorithmSelect.setItems("Минимакс", "Монте-Карло");
        algorithmSelect.setValue("Минимакс");
        algorithmSelect.getStyle()
                .set("width", "200px")
                .set("margin", "10px");

        // Выпадающий список для выбора сложности
        difficultySelect = new Select<>();
        difficultySelect.setLabel("Выберите сложность");
        difficultySelect.setItems("Легкая", "Средняя", "Сложная");
        difficultySelect.setValue("Средняя");
        difficultySelect.getStyle()
                .set("width", "200px")
                .set("margin", "10px");


        playButton = new Button("Играть", event -> {
            String selectedAlgorithm = algorithmSelect.getValue();
            String selectedDifficulty = difficultySelect.getValue();

            // Сохраняем в сессию
            UI.getCurrent().getSession().setAttribute("selectedAlgorithm", selectedAlgorithm);
            UI.getCurrent().getSession().setAttribute("selectedDifficulty", selectedDifficulty);

            // Просто переходим на страницу игры
            UI.getCurrent().navigate(KamisadoView.class);
        });

        playButton.getStyle()
                .setBackgroundColor("#0066cc")
                .setColor("white")
                .set("font-family", "cursive")
                .setFontSize("18px")
                .setFontWeight("bold")
                .setPadding("12px 30px")
                .setBorderRadius("8px")
                .setMargin("30px")
                .setCursor("pointer")
                .setTransition("all 0.3s ease");

        RouterLink play = new RouterLink("Играть", KamisadoView.class);
        play.getStyle()
                .setBackgroundColor("#0066cc")
                .setColor("white")
                .set("font-family", "cursive")
                .setFontSize("18px")
                .setFontWeight("bold")
                .setPadding("12px 30px")
                .setBorderRadius("8px")
                .setTextDecoration("none")
                .set("margin", "30px")
                .setCursor("pointer")
                .setTransition("all 0.3s ease");
        add(heading, algorithmSelect, difficultySelect,  playButton);
    }
}