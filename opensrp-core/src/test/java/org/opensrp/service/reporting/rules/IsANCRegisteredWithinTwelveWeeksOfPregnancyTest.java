package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsANCRegisteredWithinTwelveWeeksOfPregnancyTest {

    private IsANCRegisteredWithinTwelveWeeksOfPregnancy rule;

    @Before
    public void setUp() {
        rule = new IsANCRegisteredWithinTwelveWeeksOfPregnancy();
    }

    @Test
    public void shouldReturnTrueWhenANCIsRegisteredWithin12Weeks() {
        Map<String, String> reportData = create("serviceProvidedDate", "2012-10-23")
                .put("referenceDate", "2012-10-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-03-26")
                .put("referenceDate", "2013-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertTrue(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenANCIsRegisteredAfter12Weeks() {
        Map<String, String> reportData = create("serviceProvidedDate", "2013-10-23")
                .put("referenceDate", "2012-10-01")
                .map();
        boolean didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);

        reportData = create("serviceProvidedDate", "2013-03-27")
                .put("referenceDate", "2013-01-01")
                .map();
        didRuleSucceed = rule.apply(new SafeMap(reportData));

        assertFalse(didRuleSucceed);
    }


}

