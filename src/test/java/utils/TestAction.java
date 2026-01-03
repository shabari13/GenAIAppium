package utils;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

//@Data
@Builder
@Getter
public class TestAction {
    private ActionType actionType;
    private LocatorStrategy locatorStrategy;
    private String locatorValue;
    private String inputValue;
    private final Double confidence;
    @Override
    public String toString() {
        return String.format("TestAction{actionType=%s, strategy=%s, locator='%s', input='%s', confidence=%s}",
                actionType, locatorStrategy, locatorValue, inputValue, confidence);
    }
}

