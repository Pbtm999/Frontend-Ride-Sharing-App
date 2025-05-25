package org.vaadin.example;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.flowingcode.vaadin.addons.googlemaps.Markers;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route("/hello")
public class Hello extends VerticalLayout {
    private static final String API_KEY = "AIzaSyDX7UOQ5qJWDvN0qXnDwK-n5IXF7AaV0r4";

    public Hello() {
        var gmaps = new GoogleMap(API_KEY, null, "pt-PT");
        var dcc = new LatLon(41.152485941775126, -8.640777781587085);

        gmaps.setCenter(dcc);
        gmaps.setZoom( 15 );

        gmaps.setWidth(15, Unit.CM);
        gmaps.setHeight(15,Unit.CM);


        Registration registration = gmaps.addClickListener(e -> {
            var pos = new LatLon(e.getLatitude(), e.getLongitude());
            var marker = new GoogleMapMarker("Novo marcador fixo", pos, false, Markers.BLUE);

            gmaps.addMarker(marker);
        });

        add(gmaps);


    }
}