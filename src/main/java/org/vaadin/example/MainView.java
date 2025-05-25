package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import rsa.user.User;

import java.sql.Array;
import java.util.List;

@PageTitle("RSA")
@Route("home")
@RouteAlias("")
public class MainView extends AppLayout {

    private String loggedInNick;
    private String loggedInKey;

    public static class TabInfo {
        private final String label;
        private final Icon icon;
        private final Class<? extends Component> classPath;

        public TabInfo(String label, Icon icon, Class<? extends Component> classPath) {
            this.label = label;
            this.icon = icon;
            this.classPath = classPath;
        }

        public String getLabel() {
            return label;
        }

        public Icon getIcon() {
            return icon;
        }

        public Class<? extends Component> getClassPath() {
            return classPath;
        }
    }

    private void initDrawer() {
        SideNav nav = new SideNav();
        nav.getStyle().set("margin-top", "0.5rem");

        List<TabInfo> tabs = List.of(
                new TabInfo("Home", VaadinIcon.HOME.create(), MainView.class),
                new TabInfo("User", VaadinIcon.USER.create(), LoginView.class),
                new TabInfo("Pedir Boleia", VaadinIcon.CAR.create(), null),
                new TabInfo("Boleias", VaadinIcon.CAR.create(), null)
        );

        for (TabInfo tab : tabs) {
            SideNavItem item = new SideNavItem(tab.getLabel(), tab.getClassPath(), tab.getIcon());
            item.getStyle().set("margin-bottom", "0.5rem");
            nav.addItem(item);
        }

        addToDrawer(nav);
    }

    public MainView() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("RSA App | Arquitetura 2025");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        addToNavbar(toggle, title);
        initDrawer();
    }

    public String getLoggedInNick() {
        return loggedInNick;
    }

    public void setLoggedInNick(String nick) {
        loggedInNick = nick;
    }

    public String getLoggedInKey() {
        return loggedInKey;
    }

    public void setLoggedInKey(String nick) {
        loggedInKey = nick;
    }
}
