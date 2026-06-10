package com.example;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GameStatusBar extends HorizontalLayout {

    private Span messageLabel = new Span();
    private Icon statusIcon = new Icon(VaadinIcon.INFO_CIRCLE);

    public GameStatusBar() {
        add(statusIcon, messageLabel);
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        getStyle().set("background", "#0066cc")//#c9c538
                .set("padding", "10px")
                .setColor("white")
                .set("fontSize", "18px")
                .set("font-family", "cursive")
                .set("margin", "30px")
                .set("border-radius", "8px");
        setDefaultMessage();
    }

    public void setMessage(String text, boolean isError) {
        messageLabel.setText(text);
        if (isError) {
            statusIcon.setIcon(VaadinIcon.EXCLAMATION_CIRCLE);
        } else {
            statusIcon.setIcon(VaadinIcon.INFO_CIRCLE);
        }
    }

    public void setDefaultMessage() {
        setMessage("Ваши фигуры — белые. Кликните на фигуру, чтобы выбрать её", false);
    }

    public void showVictory() {
        setMessage("Вы победили!", false);
        statusIcon.setIcon(VaadinIcon.TROPHY);
        getStyle().set("background", "#ffd966");
    }
}
