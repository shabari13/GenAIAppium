package utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Builder
@Getter
@Setter // Add this annotation
@NoArgsConstructor // Add this annotation
@AllArgsConstructor // Add this annotation
public class TestAction {
    private ActionType actionType;
    private LocatorStrategy locatorStrategy;
    private String locatorValue;
    private String inputValue;
    private Double confidence; // Remove final to allow setting

    @Override
    public String toString() {
        return String.format("TestAction{actionType=%s, strategy=%s, locator='%s', input='%s', confidence=%s}",
                actionType, locatorStrategy, locatorValue, inputValue, confidence);
    }
}


