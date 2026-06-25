package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Verifies that every (topic, property) pair documented for the faker domain in doc/domains.md
 * actually resolves in the datafaker version benerator ships. This keeps the documentation from
 * drifting away from the library again -- the root cause of issue #454 -- by failing the build when
 * the docs reference a topic or property datafaker no longer provides.
 */
public class DataFakerDocTopicsIntegrationTest extends GeneratorTest {

    private static final String DOC = "doc/domains.md";

    @Test
    public void everyDocumentedTopicAndPropertyResolves() throws Exception {
        List<String[]> pairs = parseDocPairs(DOC);
        assertTrue("No faker topics parsed from " + DOC + " -- doc format changed?", pairs.size() > 100);

        List<String> failures = new ArrayList<>();
        for (String[] tp : pairs) {
            try {
                DataFakerGenerator g = new DataFakerGenerator(tp[0], tp[1]);
                g.init(context);
                if (g.generate() == null) {
                    failures.add(tp[0] + "." + tp[1] + " -> generated null");
                }
            } catch (Throwable t) {
                failures.add(tp[0] + "." + tp[1] + " -> " + t.getMessage());
            }
        }
        assertTrue("doc/domains.md documents faker topics/properties that datafaker no longer provides:\n"
                + String.join("\n", failures), failures.isEmpty());
    }

    /** Extracts the (topic, property) pairs from the "### Topic:" tables of the faker domain section. */
    static List<String[]> parseDocPairs(String path) throws Exception {
        List<String[]> pairs = new ArrayList<>();
        boolean inFaker = false;
        String topic = null;
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.startsWith("## ") && t.contains("faker domain")) { inFaker = true; continue; }
                if (t.startsWith("## ") && !t.contains("faker")) { inFaker = false; }
                if (!inFaker) { continue; }
                if (t.startsWith("### Topic:")) {
                    topic = t.substring("### Topic:".length()).trim();
                } else if (t.startsWith("###")) {
                    topic = null; // a non-topic heading (e.g. "### Supported Locales") ends the table
                } else if (topic != null && t.startsWith("|") && t.contains("|")) {
                    String prop = t.replaceAll("^\\|", "").split("\\|")[0].trim();
                    if (prop.isEmpty() || prop.equals("Property name") || prop.startsWith("---")) { continue; }
                    pairs.add(new String[]{topic, prop});
                }
            }
        }
        return pairs;
    }
}
