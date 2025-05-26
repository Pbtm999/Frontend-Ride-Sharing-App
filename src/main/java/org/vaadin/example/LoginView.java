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

/**
 * LoginView handles user login, registration, and displays user info
 * in the RSA application. It conditionally shows login form or
 * user dashboard based on current login status.
 */
@PageTitle("RSA")
@Route(layout = MainView.class)
public class LoginView extends FormLayout {

    private final ManagerService managerService;
    private MainView mainView;

    /**
     * Constructs the LoginView with a ManagerService instance for backend calls.
     *
     * @param managerService service responsible for authentication, registration, and user info
     */
    public LoginView(ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Lifecycle callback invoked when the component is attached to the UI.
     * Determines whether to show login form or user info form based on login state.
     *
     * @param attachEvent attach event information
     */
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

    /**
     * Generates a styled FormLayout container used by login, registration, and user forms.
     *
     * @return a styled FormLayout ready to add components
     */
    private FormLayout generateForm() {
        removeAll();

        getStyle().set("width", "100vw")
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

        return formLayout;
    }

    /**
     * Builds and displays the login form allowing users to input nickname and password.
     * Handles login button logic and offers navigation to the registration form.
     */
    private void buildLoginForm() {
        FormLayout formLayout = generateForm();

        H1 title = new H1("Login");
        TextField nickname = new TextField("Nickname");
        PasswordField password = new PasswordField("Password");
        nickname.setRequired(true);
        password.setRequired(true);

        Button loginButton = new Button("Login", event -> {
            String nick = nickname.getValue();
            String key = password.getValue();
            if (managerService.authenticate(nick, key)) {
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

    /**
     * Builds and displays the registration form allowing new users to create an account.
     * Handles registration logic and provides navigation back to the login form.
     */
    private void showRegisterForm() {
        FormLayout formLayout = generateForm();

        H1 title = new H1("Register");
        TextField nickname = new TextField("Nickname");
        TextField username = new TextField("Username");
        nickname.setRequired(true);
        username.setRequired(true);

        Button registerButton = new Button("Register", event -> {
            String nick = nickname.getValue();
            String user = username.getValue();
            try {
                User userRegistered = managerService.register(nick, user);
                if (userRegistered != null && mainView != null) {
                    mainView.setLoggedInNick(nick);
                    mainView.setLoggedInKey(userRegistered.getKey());
                    Notification.show("Register successful! Your key: " + userRegistered.getKey(), 8000, Notification.Position.MIDDLE);
                    buildUserForm();
                }
            } catch (RideSharingAppException e) {
                System.err.println("Registration error: " + e.getMessage());
                Notification.show("Registration failed: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        Div switchToLogin = new Div("Login instead");
        switchToLogin.getStyle()
                .set("cursor", "pointer")
                .set("color", "blue")
                .set("width", "fit-content")
                .set("text-decoration", "underline");
        switchToLogin.addClickListener(e -> buildLoginForm());

        formLayout.add(title, nickname, username, registerButton, switchToLogin);
        add(formLayout);
    }

    /**
     * Builds and displays the user info form, showing average ratings and preferred match options.
     * Allows the user to update their preferred match setting.
     */
    private void buildUserForm() {
        removeAll();

        getStyle().set("width", "100vw")
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

        // Update preferred match on selection change
        select.addValueChangeListener(event -> {
            PreferredMatch match = event.getValue();
            managerService.setPreferredMatch(loggedInNick, loggedInKey, match);
        });

        // Try to set the current preferred match value, fallback to login form on error
        try {
            select.setValue(managerService.getPreferredMatch(loggedInNick, loggedInKey));
        } catch (RideSharingAppException e) {
            System.err.println("Error fetching preferred match: " + e.getMessage());
            mainView.setLoggedInNick(null);
            mainView.setLoggedInKey(null);
            buildLoginForm();
        }

        formLayout.add(title, starsP, starsD, select);
        add(formLayout);
    }
}