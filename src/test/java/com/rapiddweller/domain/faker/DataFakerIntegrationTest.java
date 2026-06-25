package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Ignore;
import org.junit.Test;

public class DataFakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    private final String PREFIX_PATH = "com/rapiddweller/domain/faker";

    @Ignore("Fails on CI server, but works locally. Needs further investigation. (ake2l)")
    @Test
    public void testDataFaker() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker.ben.xml");
    }

    @Test(expected = IllegalArgumentError.class)
    public void testMissingProperty(){
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_errortest.ben.xml");
    }

    /**
     * Runs the example documented for the faker domain (doc/domains.md), broadened across several
     * topics, locales and an explicit type. Guards against the documentation drifting from the actual
     * class name again: the domain was migrated from java-faker to datafaker, so the generator class is
     * {@code DataFakerGenerator} (see issue #454). The script self-validates the generated row count.
     */
    @Test
    public void testDocumentedFakerDomainExample() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_doc_example.ben.xml");
    }

    /**
     * The old java-faker class {@code FakerGenerator} was removed in the datafaker migration. Using it
     * (as the documentation previously did) must fail with a clear "Class not found" error rather than
     * be silently accepted -- this is the failure originally reported in issue #454.
     */
    @Test(expected = ConfigurationError.class)
    public void testLegacyFakerGeneratorClassNameFails() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_legacy_classname.ben.xml");
    }

}
