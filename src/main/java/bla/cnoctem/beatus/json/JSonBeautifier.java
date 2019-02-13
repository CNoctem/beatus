package bla.cnoctem.beatus.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.*;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class JSonBeautifier {

    private static Logger log = LoggerFactory.getLogger(JSonBeautifier.class);

    private static final Color LBLUE = new Color(12, 154, 200);

    private static final String SPECIALS = "{}[]";

    private StyledDocument doc;

    private String rawJson;

    private StyleContext context = new StyleContext();
    private Style style;

    public JSonBeautifier(String rawJson) {
        this.rawJson = rawJson;
        style = context.addStyle("test", null);
        doc = new DefaultStyledDocument();

        for (String part : rawJson.split("\t")) {
            String json = beautify(part);
            Arrays.stream(Objects.requireNonNull(json).split("\n")).forEach(this::writeLine);
        }
    }

    public StyledDocument getDoc() {
        return doc;
    }

    private void writeLine(String line) {
        int idx = line.indexOf(":");
        if (idx == -1) {
            append(line, Color.LIGHT_GRAY);
        } else {
            String key = line.substring(0, idx);
            String val = line.substring(idx + 1);//.replace("\\", "");

            append(key, Color.ORANGE);
            append(":", Color.LIGHT_GRAY);
            if (SPECIALS.contains(val.trim())) {
                append(val, Color.LIGHT_GRAY);
                return;
            }
            String b = beautify(val);
            if (b.contains("\n")) {
                Arrays.stream(b.split("\n")).forEach(l -> writeLine("      " + l));
                return;
            } else
                append(beautify(val), LBLUE);
        }
        append("\n", null);
    }

    private void append(String s, Color c) {
        try {
            if (c != null) StyleConstants.setForeground(style, c);
            doc.insertString(doc.getLength(), s, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String beautify(String json) {
        try {
            json = json.trim();
            if (json.startsWith("\"{") && json.endsWith("}\",")) {
                json = json.substring(1, json.length() - 2);
                json = json.replace("\\", "");
            }

            if (!json.startsWith("{") && !json.endsWith("}")) {
                return json;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            DefaultPrettyPrinter.Indenter indenter =
                    new DefaultIndenter("      ", DefaultIndenter.SYS_LF);
            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            printer.indentObjectsWith(indenter);
            printer.indentArraysWith(indenter);

            String b = objectMapper.writer(printer).writeValueAsString(objectMapper.readTree(json));
            return b;
        } catch (JsonProcessingException e) {
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
