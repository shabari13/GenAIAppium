package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Image;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AICommandInterpreter {
    private final AppiumDriver driver;
    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROFILE_ID = "us.anthropic.claude-3-7-sonnet-20250219-v1:0";



    public AICommandInterpreter(AppiumDriver driver, BedrockRuntimeClient bedrockClient) {
        this.driver = driver;
        this.bedrockClient = bedrockClient;
    }

   /* public TestAction interpretCommand(String naturalLanguageCommand, String pageSource) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();



            String userMessage = String.format(
                    "You are a test automation assistant. You must respond in a specific format with no additional text or explanations.\n\n" +
                            "Based on this page source:\n```\n%s\n```\n\n" +
                            "And this command: %s\n\n" +
                            "Follow these rules:\n" +
                            "1. For buttons:\n" +
                            "   - Always use XPath that combines multiple attributes (class, text, and resource-id)\n" +
                            "   - Ensure clickable='true' is included in the locator\n" +
                            "2. For text fields:\n" +
                            "   - Use XPath with hint attribute for input fields\n" +
                            "   - Include class name 'android.widget.EditText' in the locator\n" +
                            "3. Always include element type (Button/EditText) in locator strategy\n\n" +
                            "Respond with ONLY these exact lines (no other text):\n" +
                            "ACTION: [CLICK/SENDKEYS/SWIPE/VERIFY]\n" +
                            "STRATEGY: [ID/XPATH/ACCESSIBILITY_ID/VISUAL/CLASS_NAME]\n" +
                            "LOCATOR: [exact locator value]\n" +
                            "INPUT: [value if ACTION is SENDKEYS]",
                    pageSource, naturalLanguageCommand
            );


            // Build the request body with only user message
            requestBody.putArray("messages")
                    .add(objectMapper.createObjectNode()
                            .put("role", "user")
                            .put("content", userMessage));

            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0);

            // Create and send request
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(PROFILE_ID)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBody.toString()))
                    .build();

            InvokeModelResponse response = bedrockClient.invokeModel(request);
            return parseResponse(response);
        } catch (Exception e) {
            System.err.println("Error in interpretCommand: " + e.getMessage());
            throw new RuntimeException("Failed to interpret command: " + e.getMessage(), e);
        }
    }*/


   /* private TestAction parseResponse(InvokeModelResponse response) {
        try {
            String responseBody = response.body().asUtf8String();
            System.out.println("Raw response body: " + responseBody);

            ObjectNode jsonResponse = objectMapper.readValue(responseBody, ObjectNode.class);
            String assistantResponse = jsonResponse.path("content").path(0).path("text").asText();

            System.out.println("Assistant response: " + assistantResponse);

            TestAction testAction = new TestAction();

            // Split the response into lines and process each line
            String[] lines = assistantResponse.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;

                String key = parts[0].trim().toUpperCase();
                String value = parts[1].trim();

                System.out.println("Processing line - Key: " + key + ", Value: " + value);

                switch (key) {
                    case "ACTION":
                        try {
                            testAction.setActionType(ActionType.valueOf(value.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid action type: " + value);
                        }
                        break;
                    case "STRATEGY":
                        try {
                            testAction.setLocatorStrategy(LocatorStrategy.valueOf(value.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid locator strategy: " + value);
                        }
                        break;
                    case "LOCATOR":
                        testAction.setLocatorValue(value);
                        break;
                    case "INPUT":
                        testAction.setInputValue(value);
                        break;
                }
            }

            // Print parsed values for debugging
            System.out.println("Parsed TestAction:");
            System.out.println("Action Type: " + testAction.getActionType());
            System.out.println("Locator Strategy: " + testAction.getLocatorStrategy());
            System.out.println("Locator Value: " + testAction.getLocatorValue());
            System.out.println("Input Value: " + testAction.getInputValue());

            validateTestAction(testAction);
            return testAction;

        } catch (Exception e) {
            System.err.println("Error in parseResponse: " + e.getMessage());
            throw new RuntimeException("Failed to parse model response: " + e.getMessage(), e);
        }
    }*/

/*
    public TestAction interpretCommand(String naturalLanguageCommand, String pageSource, byte[] screenshot) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            // Truncate page source if it's too long (keep only first 4000 characters)
            String truncatedPageSource = pageSource.length() > 4000 ?
                    pageSource.substring(0, 4000) + "..." :
                    pageSource;

            // Only include screenshot information if it's provided and compress it
            String screenshotSection = "";
            if (screenshot != null) {
                // Compress the screenshot before encoding
                byte[] compressedScreenshot = compressImage(screenshot);
                String base64Screenshot = Base64.getEncoder().encodeToString(compressedScreenshot);

                // Truncate base64 string if too long
                if (base64Screenshot.length() > 5000) {
                    base64Screenshot = base64Screenshot.substring(0, 5000) + "...";
                }

                screenshotSection = String.format("And this screenshot (base64):\n```\n%s\n```\n\n", base64Screenshot);
            }

            String userMessage = String.format(
                    "You are a test automation assistant. You must respond in a specific format with no additional text or explanations.\n\n" +
                            "Based on this page source:\n```\n%s\n```\n\n" +
                            "%s" + // Screenshot section
                            "And this command: %s\n\n" +
                            "Follow these rules:\n" +
                            "1. For buttons:\n" +
                            "   - Always use XPath that combines multiple attributes (class, text, and resource-id)\n" +
                            "   - Ensure clickable='true' is included in the locator\n" +
                            "2. For text fields:\n" +
                            "   - Use XPath with hint attribute for input fields\n" +
                            "   - Include class name 'android.widget.EditText' in the locator\n" +
                            "3. For book covers and items:\n" +
                            "   - Use VISUAL strategy with confidence score\n" +
                            "   - Return coordinates as JSON: {\"x\": int, \"y\": int, \"width\": int, \"height\": int}\n" +
                            "4. For SeekBar/slider elements:\n" +   // Add this new rule
                            "   - Use SET_SLIDER action type\n" +
                            "   - Input value must be between 0 and 1\n" +
                            "   - Use XPath with android.widget.SeekBar class\n" +
                            "5. For SWIPE actions:\n" +
                            "   - Input must be one of: UP, DOWN, LEFT, RIGHT\n" +
                            "   - For scrollable content, use ScrollView's resource-id in XPath\n" +
                            "   - Example: //android.widget.ScrollView[@resource-id='com.amazon.kindle:id/view_options_tab_scrollview_layout']\n" +
                            "6. Always include element type in locator strategy\n\n" +
                            "Respond with ONLY these exact lines (no other text):\n" +
                            "ACTION: [CLICK/DOUBLE_CLICK/SENDKEYS/SWIPE/VERIFY/SET_SLIDER]\n" +
                            "STRATEGY: [ID/XPATH/ACCESSIBILITY_ID/VISUAL]\n" +
                            "LOCATOR: [exact locator value or coordinates JSON if VISUAL]\n" +
                            "INPUT: [value if ACTION is SENDKEYS or SET_SLIDER]\n" +
                            "CONFIDENCE: [0-1 score if STRATEGY is VISUAL]",
                    pageSource, screenshotSection, naturalLanguageCommand
            );


            // Build the request body
            requestBody.putArray("messages")
                    .add(objectMapper.createObjectNode()
                            .put("role", "user")
                            .put("content", userMessage));

            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(PROFILE_ID)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBody.toString()))
                    .build();

            InvokeModelResponse response = bedrockClient.invokeModel(request);
            TestAction action = parseResponse(response);
            System.out.println("Generated TestAction details:");
            System.out.println("- Action Type: " + action.getActionType());
            System.out.println("- Strategy: " + action.getLocatorStrategy());
            System.out.println("- Locator: " + action.getLocatorValue());
            System.out.println("- Input: " + action.getInputValue());
            System.out.println("- Confidence: " + action.getConfidence());
            return action;
        } catch (Exception e) {
            System.err.println("Error in interpretCommand: " + e.getMessage());
            throw new RuntimeException("Failed to interpret command: " + e.getMessage(), e);
        }
    }
*/
/*
public TestAction interpretCommand(String naturalLanguageCommand, String pageSource, byte[] screenshotSection) {
    try {
        ObjectNode requestBody = objectMapper.createObjectNode();
        boolean isIOS = driver instanceof IOSDriver;

        String userMessage = isIOS ?
                createIOSPrompt(naturalLanguageCommand, pageSource, screenshotSection) :
                createAndroidPrompt(naturalLanguageCommand, pageSource, screenshotSection);

        // Build the request body
        requestBody.putArray("messages")
                .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", userMessage));

        requestBody.put("anthropic_version", "bedrock-2023-05-31");
        requestBody.put("max_tokens", 300);
        requestBody.put("temperature", 0);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(PROFILE_ID)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(requestBody.toString()))
                .build();

        InvokeModelResponse response = bedrockClient.invokeModel(request);
        TestAction action = parseResponse(response);
        logTestAction(action, isIOS);
        return action;
    } catch (Exception e) {
        System.err.println("Error in interpretCommand: " + e.getMessage());
        throw new RuntimeException("Failed to interpret command: " + e.getMessage(), e);
    }
}
*/
public TestAction interpretCommand(String naturalLanguageCommand, String pageSource) {
    try {
        // Create the request
        ObjectNode requestBody = objectMapper.createObjectNode();
        String userMessage = createPrompt(naturalLanguageCommand, pageSource);

        requestBody.putArray("messages")
                .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", userMessage));

        requestBody.put("anthropic_version", "bedrock-2023-05-31");
        requestBody.put("max_tokens", 300);
        requestBody.put("temperature", 0);

        // Send request and get response
        InvokeModelResponse response = sendRequest(requestBody);
        String responseBody = response.body().asUtf8String();

        // Debug logging
        System.out.println("Raw response: " + responseBody);

        // Extract the actual response content
        JsonNode rootNode = objectMapper.readTree(responseBody);
        return parseResponse(rootNode);

    } catch (Exception e) {
        System.err.println("Error in interpretCommand: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Failed to interpret command: " + e.getMessage(), e);
    }
}

    private TestAction parseResponse(JsonNode rootNode) {
        try {
            // Debug logging
            System.out.println("Parsing node: " + rootNode);

            // Extract the response content from the new structure
            JsonNode contentNode = rootNode.path("content");
            if (!contentNode.isMissingNode() && contentNode.isArray() && contentNode.size() > 0) {
                JsonNode firstContent = contentNode.get(0);
                if (firstContent.has("text")) {
                    String content = firstContent.get("text").asText();

                    // Try to parse the content as JSON
                    try {
                        JsonNode actionJson = objectMapper.readTree(content);
                        return extractTestAction(actionJson);
                    } catch (Exception e) {
                        // If direct parsing fails, try to extract JSON from the content
                        return extractTestActionFromContent(content);
                    }
                }
            }

            throw new RuntimeException("Unable to parse response structure");

        } catch (Exception e) {
            System.err.println("Error parsing response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    private TestAction extractTestAction(JsonNode actionJson) {
        try {
            // Convert string action to ActionType enum
            String actionStr = getNodeValueSafely(actionJson, "action");
            ActionType actionType = actionStr != null ? ActionType.valueOf(actionStr.toUpperCase()) : null;

            // Convert string locator strategy to LocatorStrategy enum
            String strategyStr = getNodeValueSafely(actionJson, "strategy");
            LocatorStrategy locatorStrategy = strategyStr != null ? LocatorStrategy.valueOf(strategyStr.toUpperCase()) : null;

            // Get other values
            String locatorValue = getNodeValueSafely(actionJson, "locator");
            String inputValue = getNodeValueSafely(actionJson, "value");

            // Get confidence if present, default to 1.0
            Double confidence = 1.0;
            JsonNode confidenceNode = actionJson.get("confidence");
            if (confidenceNode != null && !confidenceNode.isNull()) {
                confidence = confidenceNode.asDouble();
            }

            // Use the builder pattern
            return TestAction.builder()
                    .actionType(actionType)
                    .locatorStrategy(locatorStrategy)
                    .locatorValue(locatorValue)
                    .inputValue(inputValue)
                    .confidence(confidence)
                    .build();

        } catch (IllegalArgumentException e) {
            System.err.println("Error converting action type or strategy: " + e.getMessage());
            throw new RuntimeException("Invalid action or strategy type", e);
        }
    }

    private TestAction extractTestActionFromContent(String content) {
        // Try to find JSON-like structure in the content
        int startBrace = content.indexOf("{");
        int endBrace = content.lastIndexOf("}");

        if (startBrace >= 0 && endBrace > startBrace) {
            String jsonStr = content.substring(startBrace, endBrace + 1);
            try {
                JsonNode actionJson = objectMapper.readTree(jsonStr);
                return extractTestAction(actionJson);
            } catch (Exception e) {
                System.err.println("Failed to parse extracted JSON: " + jsonStr);
                throw new RuntimeException("Could not parse action JSON from content", e);
            }
        }

        throw new RuntimeException("No valid JSON structure found in content");
    }

    private String getNodeValueSafely(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private String createPrompt(String command, String pageSource) {
        boolean isIOS = driver instanceof IOSDriver;

        return String.format(
                "You are a %s test automation expert. Based on this command: '%s'\n\n" +
                        "Page source:\n```\n%s\n```\n\n" +
                        "Respond with ONLY a JSON object in this exact format:\n" +
                        "{\n" +
                        "  \"action\": \"<action_type>\",\n" +
                        "  \"strategy\": \"<locator_strategy>\",\n" +
                        "  \"locator\": \"<element_locator>\",\n" +
                        "  \"value\": \"<input_value_if_needed>\",\n" +
                        "  \"confidence\": <confidence_score>\n" +
                        "}\n\n" +
                        "Where:\n" +
                        "- action_type is one of: CLICK, TYPE, SWIPE, SCROLL, LONG_PRESS\n" +
                        "- locator_strategy is one of: XPATH, ACCESSIBILITY_ID, CLASS_NAME, ID\n" +
                        "- element_locator is a valid %s locator\n" +
                        "- input_value is optional and only needed for TYPE actions\n" +
                        "- confidence_score is a number between 0 and 1\n\n" +
                        "Do not include any other text or explanations in your response.",
                isIOS ? "iOS" : "Android",
                command,
                pageSource,
                isIOS ? "XCUITest" : "UiAutomator2"
        );
    }

    private InvokeModelResponse sendRequest(ObjectNode requestBody) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            SdkBytes payload = SdkBytes.fromUtf8String(jsonRequest);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("us.anthropic.claude-3-7-sonnet-20250219-v1:0")
                    .body(payload)
                    .build();

            return bedrockClient.invokeModel(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send request to AI service", e);
        }
    }
    private String createAndroidPrompt(String naturalLanguageCommand, String pageSource, byte[] screenshotSection) {
        return String.format(
                "You are a test automation assistant. You must respond in a specific format with no additional text or explanations.\n\n" +
                        "Based on this page source:\n```\n%s\n```\n\n" +
                        "%s" + // Screenshot section
                        "And this command: %s\n\n" +
                        "Follow these rules:\n" +
                        "1. For buttons:\n" +
                        "   - Always use XPath that combines multiple attributes (class, text, and resource-id)\n" +
                        "   - Ensure clickable='true' is included in the locator\n" +
                        "2. For text fields:\n" +
                        "   - Use XPath with hint attribute for input fields\n" +
                        "   - Include class name 'android.widget.EditText' in the locator\n" +
                        "3. For book covers and items:\n" +
                        "   - Use VISUAL strategy with confidence score\n" +
                        "   - Return coordinates as JSON: {\"x\": int, \"y\": int, \"width\": int, \"height\": int}\n" +
                        "4. For SeekBar/slider elements:\n" +
                        "   - Use SET_SLIDER action type\n" +
                        "   - Input value must be between 0 and 1\n" +
                        "   - Use XPath with android.widget.SeekBar class\n" +
                        "5. For SWIPE actions:\n" +
                        "   - Input must be one of: UP, DOWN, LEFT, RIGHT\n" +
                        "   - For scrollable content, use ScrollView's resource-id in XPath\n" +
                        "   - Example: //android.widget.ScrollView[@resource-id='com.amazon.kindle:id/view_options_tab_scrollview_layout']\n" +
                        "6. Always include element type in locator strategy\n\n" +
                        "Respond with ONLY these exact lines (no other text):\n" +
                        "ACTION: [CLICK/DOUBLE_CLICK/SENDKEYS/SWIPE/VERIFY/SET_SLIDER]\n" +
                        "STRATEGY: [ID/XPATH/ACCESSIBILITY_ID/VISUAL]\n" +
                        "LOCATOR: [exact locator value or coordinates JSON if VISUAL]\n" +
                        "INPUT: [value if ACTION is SENDKEYS or SET_SLIDER]\n" +
                        "CONFIDENCE: [0-1 score if STRATEGY is VISUAL]",
                pageSource, screenshotSection, naturalLanguageCommand
        );
    }

    private String createIOSPrompt(String command, String pageSource, byte[] screenshotSection) {
        return String.format(
                "You are an iOS test automation expert. Based on this command: '%s'\n\n" +
                        "Page source:\n```\n%s\n```\n\n" +
                        "Respond with a JSON object containing:\n" +
                        "- 'action': The type of action to perform (tap, type, swipe, etc)\n" +
                        "- 'locator': The iOS-specific locator strategy\n" +
                        "- 'value': Any input value needed\n\n" +
                        "Example response format:\n" +
                        "{\n" +
                        "  \"action\": \"tap\",\n" +
                        "  \"locator\": \"//XCUIElementTypeButton[@name='Library']\",\n" +
                        "  \"value\": null\n" +
                        "}",
                command,
                pageSource
        );
    }
/*
    private String createIOSPrompt(String naturalLanguageCommand, String pageSource,  byte[] screenshotSection) {
        return String.format(
                "You are a test automation assistant for iOS. You must respond in a specific format with no additional text or explanations.\n\n" +
                        "Based on this page source:\n```\n%s\n```\n\n" +
                        "%s" + // Screenshot section
                        "And this command: %s\n\n" +
                        "Follow these rules:\n" +
                        "1. For buttons:\n" +
                        "   - Use XPath with XCUIElementTypeButton class\n" +
                        "   - Include name attribute for text matching\n" +
                        "   - Example: //XCUIElementTypeButton[@name='Sign In']\n" +
                        "2. For text fields:\n" +
                        "   - Use XPath with XCUIElementTypeTextField or XCUIElementTypeSecureTextField\n" +
                        "   - Include name or value attributes for identification\n" +
                        "   - Example: //XCUIElementTypeTextField[@name='Email or Phone Number']\n" +
                        "3. For book covers and items:\n" +
                        "   - Use VISUAL strategy with confidence score\n" +
                        "   - Return coordinates as JSON: {\"x\": int, \"y\": int, \"width\": int, \"height\": int}\n" +
                        "4. For slider elements:\n" +
                        "   - Use SET_SLIDER action type\n" +
                        "   - Input value must be between 0 and 1\n" +
                        "   - Use XPath with XCUIElementTypeSlider class\n" +
                        "   - Example: //XCUIElementTypeSlider[@name='Brightness']\n" +
                        "5. For SWIPE actions:\n" +
                        "   - Input must be one of: UP, DOWN, LEFT, RIGHT\n" +
                        "   - For scrollable content, use XCUIElementTypeScrollView in XPath\n" +
                        "   - Example: //XCUIElementTypeScrollView[@name='Reader Settings']\n" +
                        "6. For switches and toggles:\n" +
                        "   - Use XPath with XCUIElementTypeSwitch class\n" +
                        "   - Include name attribute for identification\n" +
                        "   - Example: //XCUIElementTypeSwitch[@name='Auto-Brightness']\n" +
                        "7. Always include element type in locator strategy\n\n" +
                        "Respond with ONLY these exact lines (no other text):\n" +
                        "ACTION: [CLICK/DOUBLE_CLICK/SENDKEYS/SWIPE/VERIFY/SET_SLIDER]\n" +
                        "STRATEGY: [ID/XPATH/ACCESSIBILITY_ID/VISUAL]\n" +
                        "LOCATOR: [exact locator value or coordinates JSON if VISUAL]\n" +
                        "INPUT: [value if ACTION is SENDKEYS or SET_SLIDER]\n" +
                        "CONFIDENCE: [0-1 score if STRATEGY is VISUAL]",
                pageSource, screenshotSection, naturalLanguageCommand
        );
    }
*/

    private void logTestAction(TestAction action, boolean isIOS) {
        System.out.println("Generated TestAction details for " + (isIOS ? "iOS" : "Android") + ":");
        System.out.println("- Action Type: " + action.getActionType());
        System.out.println("- Strategy: " + action.getLocatorStrategy());
        System.out.println("- Locator: " + action.getLocatorValue());
        System.out.println("- Input: " + action.getInputValue());
        System.out.println("- Confidence: " + action.getConfidence());
    }

    private TestAction parseResponse(InvokeModelResponse response) {
        try {
            String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String content = jsonNode.get("completion").asText();

            TestAction action = new TestAction();

            for (String line : content.split("\n")) {
                if (line.startsWith("ACTION:")) {
                    action.setActionType(ActionType.valueOf(line.substring(8).trim()));
                } else if (line.startsWith("STRATEGY:")) {
                    action.setLocatorStrategy(LocatorStrategy.valueOf(line.substring(10).trim()));
                } else if (line.startsWith("LOCATOR:")) {
                    action.setLocatorValue(line.substring(9).trim());
                } else if (line.startsWith("INPUT:")) {
                    action.setInputValue(line.substring(7).trim());
                } else if (line.startsWith("CONFIDENCE:")) {
                    String confidenceStr = line.substring(11).trim();
                    if (!confidenceStr.isEmpty()) {
                        action.setConfidence(Double.parseDouble(confidenceStr));
                    }
                }
            }

            return action;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
    private byte[] compressImage(byte[] imageData) {
        try {
            // Read the original image
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));

            // Scale down the image if it's too large
            int targetWidth = 800;  // Adjust these values as needed
            int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));

            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);

            // Write to byte array with compression
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Use JPEG format with compression
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.5f);  // Adjust compression level (0.0-1.0)

            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(resizedImage, null, null), params);

            writer.dispose();
            imageOutputStream.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress image: " + e.getMessage(), e);
        }
    }

 /*   private TestAction parseResponse(InvokeModelResponse response) {
        try {
            String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String content = jsonResponse.get("content").get(0).get("text").asText();

            Map<String, String> resultMap = new HashMap<>();
            for (String line : content.split("\n")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    resultMap.put(parts[0].trim(), parts[1].trim());
                }
            }

            return TestAction.builder()
                    .actionType(ActionType.valueOf(resultMap.get("ACTION")))
                    .locatorStrategy(LocatorStrategy.valueOf(resultMap.get("STRATEGY")))
                    .locatorValue(resultMap.get("LOCATOR"))
                    .inputValue(resultMap.get("INPUT"))
                    .confidence("VISUAL".equals(resultMap.get("STRATEGY")) ?
                            Double.parseDouble(resultMap.get("CONFIDENCE")) : null)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }*/

    private void validateTestAction(TestAction testAction) {
        List<String> errors = new ArrayList<>();

        if (testAction.getActionType() == null) {
            errors.add("Invalid or missing ACTION in response");
        }
        if (testAction.getLocatorStrategy() == null) {
            errors.add("Invalid or missing STRATEGY in response");
        }
        if (testAction.getLocatorValue() == null || testAction.getLocatorValue().isEmpty()) {
            errors.add("Missing LOCATOR value in response");
        }
        if (testAction.getActionType() == ActionType.SENDKEYS &&
                (testAction.getInputValue() == null || testAction.getInputValue().isEmpty())) {
            errors.add("Missing INPUT value for SENDKEYS action");
        }

        if (!errors.isEmpty()) {
            System.err.println("Validation errors: " + String.join(", ", errors));
            throw new RuntimeException("Validation errors: " + String.join(", ", errors));
        }
    }
}
