package org.vaadin.example;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * MainView is the primary layout of the RSA application.
 * It provides a navigation drawer with tabs for Home, User, and Ride Map views,
 * and manages global application state like the logged-in user and selected rides.
 */
@PageTitle("RSA")
@Route("")
public class MainView extends AppLayout {

    private String loggedInNick;
    private String loggedInKey;
    private long actualRideId = -1;
    private GoogleMapMarker toMarker;
    private boolean matchSelected = false;

    /**
     * TabInfo record holds label, icon, and target view class for navigation tabs.
     */
    public record TabInfo(String label, Icon icon, Class<? extends Component> classPath) {
    }

    /**
     * Initializes the navigation drawer with predefined tabs.
     */
    private void initDrawer() {
        SideNav nav = new SideNav();
        nav.getStyle().set("margin-top", "0.5rem");

        List<TabInfo> tabs = List.of(
                new TabInfo("Home", VaadinIcon.HOME.create(), HomeView.class),
                new TabInfo("User", VaadinIcon.USER.create(), LoginView.class),
                new TabInfo("Boleias", VaadinIcon.CAR.create(), RideMapView.class)
        );

        for (TabInfo tab : tabs) {
            SideNavItem item = new SideNavItem(tab.label(), tab.classPath(), tab.icon());
            item.getStyle().set("margin-bottom", "0.5rem");
            nav.addItem(item);
        }

        addToDrawer(nav);
    }

    /**
     * Constructs the MainView layout, adding navbar and drawer.
     * Sets initial content with a brief description of the app.
     */
    public MainView() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("RSA App | Arquitetura 2025");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        addToNavbar(toggle, title);
        initDrawer();

        H1 mainTitle = new H1("RSA App | Ride Service Application");
        Paragraph paragraph = new Paragraph(
                "RSA is a ride service application developed for a Arquitetura Software class of CC course in FCUP."
        );
        Paragraph paragraph2 = new Paragraph(
                "It is developed using Java SDK 21 and Vaadin + Spring Framework."
        );

        VerticalLayout view = new VerticalLayout();
        view.add(mainTitle, paragraph, paragraph2);
        setContent(view);
    }

    /** @return logged-in user's nickname */
    public String getLoggedInNick() {
        return loggedInNick;
    }

    /** Sets the logged-in user's nickname */
    public void setLoggedInNick(String nick) {
        loggedInNick = nick;
    }

    /** @return logged in user's key */
    public String getLoggedInKey() {
        return loggedInKey;
    }

    /** Sets the logged-in user's key */
    public void setLoggedInKey(String key) {
        loggedInKey = key;
    }

    /** Sets the current active ride ID */
    public void setActualRideId(long rideId) {
        actualRideId = rideId;
    }

    /** @return the current active ride ID */
    public long getActualRideId() {
        return actualRideId;
    }

    /** @return marker pointing to destination location on the map */
    public GoogleMapMarker getToMarker() {
        return toMarker;
    }

    /** Sets the destination marker on the map */
    public void setToMarker(GoogleMapMarker toMarker) {
        this.toMarker = toMarker;
    }

    /** @return true if a match is currently selected */
    public boolean isMatchSelected() {
        return matchSelected;
    }

    /** Sets whether a match is currently selected */
    public void setMatchSelected(boolean matchSelected) {
        this.matchSelected = matchSelected;
    }
}