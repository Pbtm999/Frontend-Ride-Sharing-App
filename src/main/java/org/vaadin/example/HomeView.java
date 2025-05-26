package org.vaadin.example;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * HomeView serves as the landing page for the RSA app,
 * displaying basic information about the application.
 */
@PageTitle("RSA")
@Route(layout = MainView.class)
public class HomeView extends VerticalLayout {

    /**
     * Constructs the HomeView with a title and descriptive paragraphs.
     */
    public HomeView() {
        H1 mainTitle = new H1("RSA App | Ride Service Application");

        Paragraph paragraph = new Paragraph(
                "RSA is a ride service application developed for the Arquitetura Software " +
                        "class of the CC course in FCUP."
        );

        Paragraph paragraph2 = new Paragraph(
                "It is developed using Java SDK 21 and Vaadin + Spring Framework."
        );

        add(mainTitle, paragraph, paragraph2);
    }
}
