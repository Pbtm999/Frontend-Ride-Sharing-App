package org.vaadin.example;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.flowingcode.vaadin.addons.googlemaps.Markers;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import rsa.RideSharingAppException;
import rsa.match.Location;
import rsa.match.RideMatch;
import rsa.user.UserStars;

import java.util.Set;

import static rsa.ride.RideRole.DRIVER;
import static rsa.ride.RideRole.PASSENGER;

/**
 * View displaying the ride map interface with Google Maps integration.
 * Allows users to select destinations, start rides, view and accept matches, and rate rides.
 */
@PageTitle("RSA")
@Route(layout = MainView.class)
public class RideMapView extends VerticalLayout {
    private static final String API_KEY = ""
    private GoogleMapMarker fromMarker;
    private LatLon pos = null;
    private final VerticalLayout mapLayout = new VerticalLayout();
    private final GoogleMap gmaps;
    private final Button addRide;

    /**
     * Called when the component is attached to the UI.
     * Sets up the map layout and markers depending on the current ride state.
     * @param attachEvent the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        this.mainView = (MainView) getParent().orElse(null);

        if ( mainView != null && mainView.getActualRideId() != -1) {
            mapLayout.add(gmaps);

            gmaps.addMarker(mainView.getToMarker());
        } else {
            mapLayout.add(gmaps, addRide);
        }
    }

    /**
     * Constructs the RideMapView, initializes the Google Map, form layout and event listeners.
     * @param managerService the service to manage rides and matches
     */
    public RideMapView(ManagerService managerService) {

        HorizontalLayout main = new HorizontalLayout();

        this.gmaps = new GoogleMap(API_KEY, null, "pt-PT");
        var dcc = new LatLon(41.152485941775126, -8.640777781587085);
        gmaps.setCenter(dcc);

        gmaps.addClickListener(event -> {
            if (mainView.getActualRideId() != -1)
                return;

            pos = new LatLon(event.getLatitude(), event.getLongitude());
            GoogleMapMarker toMarker = mainView.getToMarker();
            if (toMarker != null)
                gmaps.removeMarker(toMarker);

            toMarker = new GoogleMapMarker("Destination", pos, true, Markers.ORANGE);
            mainView.setToMarker(toMarker);

            toMarker.addDragEndEventListener( de -> {

                if (mainView != null) {
                    if (mainView.getActualRideId() == -1)
                        pos = new LatLon(de.getLatitude(), de.getLongitude());
                }
            });

            gmaps.addMarker(toMarker);
        });

        FormLayout form = new FormLayout();
        form.getStyle()
                .set("margin-top", "50px")
                .set("width", "fit-content")
                .set("height", "fit-content")
                .set("padding", "2rem")
                .set("background-color", "white")
                .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "10px")
                .set("justify-self", "center")
                .set("transition", "width .4s ease");

        this.addRide = new Button("Start Ride", event -> {

            if (mainView != null) {
                String nick = mainView.getLoggedInNick();
                String key = mainView.getLoggedInKey();
                if (nick != null && key != null) {
                    GoogleMapMarker toMarker = mainView.getToMarker();
                    if (toMarker != null) {
                        gmaps.setWidth(25, Unit.CM);
                        main.add(form);
                    } else {
                        Notification.show("You need to select a location to go first!", 3000, Notification.Position.MIDDLE);
                    }

                } else {
                    Notification.show("You should login first!", 3000, Notification.Position.MIDDLE);
                    UI.getCurrent().navigate(LoginView.class);
                }
            }

        });

        gmaps.addCurrentLocationEventListener( e-> {
            var source = e.getSource();
            var here = new LatLon(source.getLatitude(), source.getLongitude());
            fromMarker = new GoogleMapMarker("Here",here,false,Markers.BLUE);

            gmaps.addMarker(fromMarker);

            if ( mainView != null && mainView.getActualRideId() != -1) {
                Location current = new Location(here.getLon(), here.getLat());
                Set<RideMatch> rideMatches = managerService.updateRide(mainView.getActualRideId(), current);
                if (rideMatches != null && !rideMatches.isEmpty()) {
                    gmaps.setWidth(25, Unit.CM);
                    FormLayout formLayout = new FormLayout();

                    formLayout.getStyle()
                            .set("margin-top", "50px")
                            .set("width", "fit-content")
                            .set("height", "fit-content")
                            .set("padding", "2rem")
                            .set("background-color", "white")
                            .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                            .set("border-radius", "10px")
                            .set("justify-self", "center")
                            .set("transition", "width .4s ease");

                    H1 title = new H1("Matches");
                    Select<RideMatch> matchSelect = new Select<>();

                    matchSelect.setItems(rideMatches);

                    matchSelect.setItemLabelGenerator(match -> "Match #" + match.getId());

                    matchSelect.setPlaceholder("Choose a match");

                    TextField costField = new TextField("Cost");
                    TextField starsField = new TextField("Stars");
                    costField.setReadOnly(true);
                    starsField.setReadOnly(true);

                    RideMatch firstMatch = rideMatches.stream().findFirst().orElse(null);

                    matchSelect.addValueChangeListener(event -> {
                        RideMatch selectedMatch = event.getValue();
                        if (selectedMatch != null) {

                            costField.setValue(String.valueOf(selectedMatch.getCost()));

                            if (firstMatch != null) {
                                if (firstMatch.getRide(PASSENGER).getId() == mainView.getActualRideId())
                                    starsField.setValue(String.valueOf(selectedMatch.getStars(DRIVER)));
                                else
                                    starsField.setValue(String.valueOf(selectedMatch.getStars(PASSENGER)));
                            }
                        } else {
                            costField.clear();
                            starsField.clear();
                        }
                    });

                    Select<UserStars> starsSelect = new Select<>();
                    starsSelect.setLabel("Rate the ride");

                    starsSelect.setItems(UserStars.values());
                    starsSelect.setItemLabelGenerator(star -> switch (star) {
                        case FIVE_STARS -> "★★★★★";
                        case FOUR_STARS -> "★★★★☆";
                        case THREE_STARS -> "★★★☆☆";
                        case TWO_STARS -> "★★☆☆☆";
                        case ONE_STAR -> "★☆☆☆☆";
                    });

                    Button submit = new Button("Accept Match", event -> {
                        if (!mainView.isMatchSelected()) {
                            managerService.acceptMatch(mainView.getActualRideId(), matchSelect.getValue().getId());
                            formLayout.remove(title, matchSelect, costField, starsField);
                            mainView.setMatchSelected(true);

                            formLayout.add(starsSelect);
                        } else {
                            managerService.concludeRide(mainView.getActualRideId(), starsSelect.getValue());
                            mainView.setMatchSelected(false);
                            mainView.setActualRideId(-1);
                            gmaps.removeMarker(mainView.getToMarker());
                            mainView.setToMarker(null);
                            main.remove(formLayout);
                            gmaps.setWidth(40, Unit.CM);
                            mapLayout.add(addRide);
                        }

                    });

                    submit.getStyle()
                            .set("cursor", "pointer")
                            .set("backgroundColor", "blue")
                            .set("color", "white");

                    formLayout.add(title, matchSelect, costField, starsField, submit);

                    main.add(formLayout);
                }
            }
        });
        gmaps.goToCurrentLocation();

        gmaps.addLocationTrackingActivatedEventListener( e -> {
            var source = e.getSource();
            var here = new LatLon(source.getLatitude(), source.getLongitude());

            fromMarker.setPosition(here);
            if (mainView != null) {
                if (mainView.getActualRideId() != -1) {
                    Location current = new Location(here.getLon(), here.getLat());
                    Set<RideMatch> rideMatches = managerService.updateRide(mainView.getActualRideId(), current);
                    if (rideMatches != null && !rideMatches.isEmpty()) {
                        gmaps.setWidth(25, Unit.CM);
                        FormLayout formLayout = new FormLayout();

                        formLayout.getStyle()
                                .set("margin-top", "50px")
                                .set("width", "fit-content")
                                .set("height", "fit-content")
                                .set("padding", "2rem")
                                .set("background-color", "white")
                                .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
                                .set("border-radius", "10px")
                                .set("justify-self", "center")
                                .set("transition", "width .4s ease");

                        H1 title = new H1("Matches");
                        Select<RideMatch> matchSelect = new Select<>();

                        matchSelect.setItems(rideMatches);

                        matchSelect.setItemLabelGenerator(match -> "Match #" + match.getId());

                        matchSelect.setPlaceholder("Choose a match");

                        TextField costField = new TextField("Cost");
                        TextField starsField = new TextField("Stars");
                        costField.setReadOnly(true);
                        starsField.setReadOnly(true);

                        RideMatch firstMatch = rideMatches.stream().findFirst().orElse(null);

                        matchSelect.addValueChangeListener(event -> {
                            RideMatch selectedMatch = event.getValue();
                            if (selectedMatch != null) {

                                costField.setValue(String.valueOf(selectedMatch.getCost()));

                                if (firstMatch != null) {
                                    if (firstMatch.getRide(PASSENGER).getId() == mainView.getActualRideId())
                                        starsField.setValue(String.valueOf(selectedMatch.getStars(DRIVER)));
                                    else
                                        starsField.setValue(String.valueOf(selectedMatch.getStars(PASSENGER)));
                                }
                            } else {
                                costField.clear();
                                starsField.clear();
                            }
                        });

                        Select<UserStars> starsSelect = new Select<>();
                        starsSelect.setLabel("Rate the ride");

                        starsSelect.setItems(UserStars.values());
                        starsSelect.setItemLabelGenerator(star -> switch (star) {
                            case FIVE_STARS -> "★★★★★";
                            case FOUR_STARS -> "★★★★☆";
                            case THREE_STARS -> "★★★☆☆";
                            case TWO_STARS -> "★★☆☆☆";
                            case ONE_STAR -> "★☆☆☆☆";
                        });

                        Button submit = new Button("Accept Match", event -> {
                            if (!mainView.isMatchSelected()) {
                                managerService.acceptMatch(mainView.getActualRideId(), matchSelect.getValue().getId());
                                formLayout.remove(title, matchSelect, costField, starsField);
                                mainView.setMatchSelected(true);

                                formLayout.add(starsSelect);
                            } else {
                                managerService.concludeRide(mainView.getActualRideId(), starsSelect.getValue());
                                mainView.setMatchSelected(false);
                                mainView.setActualRideId(-1);
                                gmaps.removeMarker(mainView.getToMarker());
                                mainView.setToMarker(null);
                                main.remove(formLayout);
                                gmaps.setWidth(40, Unit.CM);
                                mapLayout.add(addRide);
                            }

                        });

                        submit.getStyle()
                                .set("cursor", "pointer")
                                .set("backgroundColor", "blue")
                                .set("color", "white");

                        formLayout.add(title, matchSelect, costField, starsField, submit);

                        main.add(formLayout);
                    }
                }
            }
        } );

        gmaps.setZoom( 15 );

        gmaps.setWidth(40, Unit.CM);
        gmaps.setHeight(20,Unit.CM);

        H1 title = new H1("Add Ride");
        TextField plate = new TextField("Plate (fill it only if you are a driver)");
        TextField cost = new TextField("Cost");
        cost.setValue("0");

        Button submit = new Button("Submit", event -> {

            if  (mainView != null) {
                try {
                    Location to = new Location(pos.getLon(), pos.getLat());
                    Location from = new Location(fromMarker.getPosition().getLon(), fromMarker.getPosition().getLat());

                    var plateValue = plate.getValue();
                    if (plateValue.isEmpty())
                        plateValue = null;

                    mainView.setActualRideId(managerService.addRide(mainView.getLoggedInNick(), mainView.getLoggedInKey(), from, to, plateValue, Float.parseFloat(cost.getValue())));
                    main.remove(form);
                    mapLayout.remove(addRide);
                    gmaps.setWidth(40, Unit.CM);

                    GoogleMapMarker toMarker = mainView.getToMarker();
                    LatLon toPos = toMarker.getPosition();
                    gmaps.removeMarker(toMarker);
                    toMarker = new GoogleMapMarker("Destination", toPos, false, Markers.ORANGE);
                    mainView.setToMarker(toMarker);
                    gmaps.addMarker(toMarker);

                } catch (RideSharingAppException e) {
                    Notification.show("You should login first!", 3000, Notification.Position.MIDDLE);
                    UI.getCurrent().navigate(LoginView.class);
                }
            } else {
                UI.getCurrent().navigate(MainView.class);
            }

        });

        submit.getStyle()
                .set("cursor", "pointer")
                .set("backgroundColor", "blue")
                .set("color", "white");

        addRide.getStyle()
                .set("cursor", "pointer")
                .set("backgroundColor", "blue")
                .set("color", "white");

        main.add(mapLayout);

        form.add(title, plate, cost, submit);

        add(main);
    }
}