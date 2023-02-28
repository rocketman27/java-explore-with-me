package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StateAction {
    SEND_TO_REVIEW("SEND_TO_REVIEW"),
    CANCEL_REVIEW("CANCEL_REVIEW"),
    PUBLISH_EVENT("PUBLISH_EVENT"),
    REJECT_EVENT("REJECT_EVENT");

    private String value;

    StateAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StateAction fromValue(String value) {
        for (StateAction b : StateAction.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
