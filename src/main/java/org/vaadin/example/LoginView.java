package org.vaadin.example;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import rsa.RideSharingAppException;
import rsa.match.PreferredMatch;
import rsa.user.User;

import static rsa.match.PreferredMatch.*;
import static rsa.ride.RideRole.DRIVER;
import static rsa.ride.RideRole.PASSENGER;

@PageTitle("RSA")
@Route(layout = MainView.class)
public class LoginView extends FormLayout {

    private final ManagerService usersService;
    private final ManagerService managerService;
    private MainView mainView;

    public LoginView(ManagerService usersService, ManagerService managerService) {
        this.usersService = usersService;
        this.managerService = managerService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        this.mainView = (MainView) getParent().orElse(null);

        if (mainView != null) {
            String loggedInNick = mainView.getLoggedInNick();
            if (loggedInNick == null) {
                buildLoginForm();
            } else {
                buildUserForm();
            }
        } else {
            UI.getCurrent().navigate("");
        }
    }

    private void buildLoginForm() {
        removeAll();
        getStyle().set("width", "100vw");
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center");

        FormLayout formLayout = new FormLayout();

        formLayout.removeAll();

        formLayout.getStyle()
                .set("margin-top", "50px")
                .set("width", "fit-content")
                .set("height", "fit-content")
                .set("padding", "2rem")
                .set("background-color", "white")
                .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "10px")
                .set("justify-self", "center");

        H1 title = new H1("Login");
        TextField nickname = new TextField("Nickname");
        PasswordField password = new PasswordField("Password");
        nickname.setRequired(true);
        password.setRequired(true);

        Button loginButton = new Button("Login", event -> {
            String nick = nickname.getValue();
            String key = password.getValue();
            if (usersService.authenticate(nick, key)) {
                if (mainView != null) {
                    mainView.setLoggedInNick(nick);
                    mainView.setLoggedInKey(key);

                    Notification.show("Login successful!", 3000, Notification.Position.MIDDLE);
                    buildUserForm();
                }
            }
        });

        Div switchToRegister = new Div("Create a new user");
        switchToRegister.getStyle()
                .set("cursor", "pointer")
                .set("color", "blue")
                .set("width", "fit-content")
                .set("text-decoration", "underline");
        switchToRegister.addClickListener(e -> showRegisterForm());

        formLayout.add(title, nickname, password, loginButton, switchToRegister);

        add(formLayout);
    }

    private void showRegisterForm() {
        removeAll();
        getStyle()
                .set("width", "100vw")
                .set("display", "flex")
                .set("justify-content", "center");

        FormLayout formLayout = new FormLayout();

        formLayout.getStyle()
                .set("margin-top", "50px")
                .set("width", "fit-content")
                .set("height", "fit-content")
                .set("padding", "2rem")
                .set("background-color", "white")
                .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "10px")
                .set("justify-self", "center");

        H1 title = new H1("Register");
        TextField nickname = new TextField("Nickname");
        TextField username = new TextField("Username");
        nickname.setRequired(true);
        username.setRequired(true);

        Button registerButton = new Button("Register", event -> {
            String nick = nickname.getValue();
            String user = username.getValue();
            try {
                User userRegisted = usersService.register(nick, user);
                if (userRegisted != null) {

                    if (mainView != null) {
                        mainView.setLoggedInNick(nick);
                        mainView.setLoggedInKey(userRegisted.getKey());

                        Notification.show("Register successful! Your key: " + userRegisted.getKey(), 8000, Notification.Position.MIDDLE);
                        buildUserForm();
                    }
                }
            } catch (RideSharingAppException e) {
                System.out.println("error: " + e.getMessage());
            }

        });

        Div switchToRegister = new Div("Login instead");
        switchToRegister.getStyle()
                .set("cursor", "pointer")
                .set("color", "blue")
                .set("width", "fit-content")
                .set("text-decoration", "underline");
        switchToRegister.addClickListener(e -> buildLoginForm());

        formLayout.add(title, nickname, username, registerButton, switchToRegister);

        add(formLayout);
    }

    private void buildUserForm() {
        removeAll();

        getStyle().set("width", "100vw");
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center");

        FormLayout formLayout = new FormLayout();

        formLayout.removeAll();

        formLayout.getStyle()
                .set("margin-top", "50px")
                .set("width", "fit-content")
                .set("height", "fit-content")
                .set("padding", "2rem")
                .set("background-color", "white")
                .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "10px")
                .set("justify-self", "center");

        String loggedInNick = mainView.getLoggedInNick();
        String loggedInKey = mainView.getLoggedInKey();

        H1 title = new H1("User: " + loggedInNick);

        TextField starsP = new TextField("Average passenger rating:");
        starsP.setValue(String.valueOf(managerService.getAverage(loggedInNick, PASSENGER)));
        starsP.setReadOnly(true);

        TextField starsD = new TextField("Average rider rating:");
        starsD.setValue(String.valueOf(managerService.getAverage(loggedInNick, DRIVER)));
        starsD.setReadOnly(true);

        Select<PreferredMatch> select = new Select<>();
        select.setLabel("Preferred Match");
        select.setItems(BETTER, CHEAPER, CLOSER);

        select.addValueChangeListener(event -> {
            PreferredMatch match = event.getValue();

            managerService.setPreferredMatch(loggedInNick, loggedInKey, match);
        });

        try {
            select.setValue(managerService.getPreferredMatch(loggedInNick, loggedInKey));
        } catch (RideSharingAppException e) {
            System.out.println("error: " + e.getMessage());
            mainView.setLoggedInNick(null);
            mainView.setLoggedInKey(null);
            buildLoginForm();
        }

        //257e35d5-247c-330e-9626-e898367a1061

        formLayout.add(title, starsP, starsD, select);
        add(formLayout);
    }
}
