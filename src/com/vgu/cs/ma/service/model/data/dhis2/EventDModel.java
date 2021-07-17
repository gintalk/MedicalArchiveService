package com.vgu.cs.ma.service.model.data.dhis2;

import com.vgu.cs.common.util.CollectionUtils;
import com.vgu.cs.engine.entity.dhis2.model.Event;
import com.vgu.cs.engine.entity.dhis2.model.Events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDModel extends Dhis2BaseDModel {

    public static final EventDModel INSTANCE = new EventDModel();

    private EventDModel() {

    }

    public List<Event> getEvents(String trackedEntityInstanceId, String programId) {
        Map<String, String> query = new HashMap<>();
        query.put("trackedEntityInstance", trackedEntityInstanceId);
        query.put("program", programId);

        return getJsonList("events", query, Events.class).getEvents();
    }

    public Event getEvent(String eventId) {
        Map<String, String> query = new HashMap<>();
        query.put("event", eventId);

        Events events = getJsonList("events", query, Events.class);
        if (events == null || CollectionUtils.isNullOrEmpty(events.getEvents())) {
            return null;
        }

        return events.getEvents().get(0);
    }
}
