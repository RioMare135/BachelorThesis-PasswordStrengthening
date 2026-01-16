package ui;

import com.nulabinc.zxcvbn.Strength;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import data.Vocab;
import evaluator.PasswordEvaluator;
import metrics.StrengthDiff;
import metrics.StrengthSnapshot;
import tokenizer.Tokenizer;
import Transform.*;

import java.util.ArrayList;
import java.util.List;

public final class PasswordApp extends Application {
    private static final int MIN_ACCEPTED_SCORE = 3;

    private final PasswordEvaluator evaluator = new PasswordEvaluator();
    private final Tokenizer tokenizer = new Tokenizer();
    private final List<TokenTransformer> transformers = List.of(
            new AddTokenTransformer(Vocab.defaultVocabulary()),
            new ReplaceTokenTransformer(Vocab.defaultVocabulary()),
            new SwapTokenTransformer()
    );

    private StrengthSnapshot current;      // current evaluation snapshot
    private StrengthSnapshot previous;     // previous snapshot for diff display

    @Override
    public void start(Stage stage) {
        Label title = new Label("Password Strength Enhancer (zxcvbn)");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        TextField relatedField = new TextField();
        relatedField.setPromptText("Related words to penalize (comma-separated), optional");

        Label statusLabel = new Label("Enter a password and click Evaluate.");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefRowCount(14);

        Button evaluateBtn = new Button("Evaluate");
        Button strengthenOneBtn = new Button("Strengthen (one method)");
        Button strengthenAllBtn = new Button("Strengthen (all three)");
        strengthenOneBtn.setDisable(true);
        strengthenAllBtn.setDisable(true);

        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("add token", "replace token", "swap token");
        methodBox.getSelectionModel().selectFirst();
        methodBox.setDisable(true);

        // Helpers
        Runnable refreshButtons = () -> {
            boolean hasPw = current != null;
            boolean weak = hasPw && current.score() < MIN_ACCEPTED_SCORE;
            strengthenOneBtn.setDisable(!weak);
            strengthenAllBtn.setDisable(!weak);
            methodBox.setDisable(!weak);
        };

        evaluateBtn.setOnAction(e -> {
            String pw = passwordField.getText();
            if (pw == null || pw.isBlank()) {
                statusLabel.setText("Please enter a password.");
                return;
            }
            List<String> userInputs = parseRelated(relatedField.getText());

            Strength s = evaluator.evaluate(pw, userInputs);
            previous = current;
            current = StrengthSnapshot.from(pw, s);

            outputArea.setText(renderSnapshot("Current", current));
            if (previous != null) {
                outputArea.appendText("\n" + new StrengthDiff(previous, current).summary());
            }

            statusLabel.setText(current.score() >= MIN_ACCEPTED_SCORE
                    ? "Password meets threshold."
                    : "Password is below threshold. Choose a strengthening option.");
            refreshButtons.run();
        });

        strengthenOneBtn.setOnAction(e -> {
            if (current == null) return;
            List<String> userInputs = parseRelated(relatedField.getText());

            int idx = methodBox.getSelectionModel().getSelectedIndex();
            TokenTransformer t = transformers.get(idx);

            List<String> tokens = tokenizer.tokenize(current.password());
            List<String> transformed = t.transform(tokens);
            String newPw = tokenizer.detokenize(transformed);

            Strength s = evaluator.evaluate(newPw, userInputs);
            previous = current;
            current = StrengthSnapshot.from(newPw, s);

            passwordField.setText(newPw); // user sees the new password (can choose to copy)
            outputArea.setText(renderSnapshot("Current (after " + t.name() + ")", current));
            outputArea.appendText("\n" + new StrengthDiff(previous, current).summary());

            statusLabel.setText(current.score() >= MIN_ACCEPTED_SCORE
                    ? "Password now meets threshold."
                    : "Still below threshold. You can strengthen again.");
            refreshButtons.run();
        });

        strengthenAllBtn.setOnAction(e -> {
            if (current == null) return;
            List<String> userInputs = parseRelated(relatedField.getText());

            List<String> tokens = tokenizer.tokenize(current.password());
            List<String> transformed = tokens;
            for (TokenTransformer t : transformers) {
                transformed = t.transform(transformed);
            }
            String newPw = tokenizer.detokenize(transformed);

            Strength s = evaluator.evaluate(newPw, userInputs);
            previous = current;
            current = StrengthSnapshot.from(newPw, s);

            passwordField.setText(newPw);
            outputArea.setText(renderSnapshot("Current (after pipeline)", current));
            outputArea.appendText("\n" + new StrengthDiff(previous, current).summary());

            statusLabel.setText(current.score() >= MIN_ACCEPTED_SCORE
                    ? "Password now meets threshold."
                    : "Still below threshold. You can strengthen again.");
            refreshButtons.run();
        });

        HBox evalRow = new HBox(10, evaluateBtn);
        HBox strengthenRow = new HBox(10, strengthenOneBtn, methodBox, strengthenAllBtn);
        VBox root = new VBox(10,
                title,
                new Label("Password"),
                passwordField,
                new Label("Related words (optional)"),
                relatedField,
                evalRow,
                strengthenRow,
                statusLabel,
                outputArea
        );
        root.setPadding(new Insets(12));

        stage.setScene(new Scene(root, 820, 520));
        stage.setTitle("Password Strength Enhancer");
        stage.show();
    }

    private static List<String> parseRelated(String s) {
        List<String> out = new ArrayList<>();
        if (s == null) return out;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return out;
        for (String part : trimmed.split(",")) {
            String v = part.trim();
            if (!v.isEmpty()) out.add(v);
        }
        return out;
    }

    private static String renderSnapshot(String label, StrengthSnapshot snap) {
        return """
                --- %s ---
                Password: %s
                Score   : %d (0..4)
                Guesses : %.3g

                Crack time (online throttled 100/h): %s
                Crack time (online 10/s)           : %s
                Crack time (offline slow 1e4/s)    : %s
                Crack time (offline fast 1e10/s)   : %s
                """.formatted(
                label,
                snap.password(),
                snap.score(),
                snap.guesses(),
                snap.crackTimeOnlineThrottling100perHour(),
                snap.crackTimeOnlineNoThrottling10perSecond(),
                snap.crackTimeOfflineSlowHashing1e4perSecond(),
                snap.crackTimeOfflineFastHashing1e10perSecond()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
