package de.ingrid.interfaces.csw.index;

import org.eclipse.jdt.internal.core.Assert;
import org.junit.Test;

public class StatusProviderTest {

    @Test
    public void testStatusProvider() throws Exception {
        StatusProvider provider = new StatusProvider();

        provider.addState("key 1", "this is state 1");
        Thread.sleep(1000);
        provider.addState("key 2", "this is state 2");

        Assert.isTrue(provider.toString().contains("state 1"));
        Assert.isTrue(provider.toString().contains("state 2"));

        provider.addState("key 1", "this is a modified state 1");

        Assert.isTrue(provider.toString().contains("modified state 1"));
        Assert.isTrue(!provider.toString().contains("is state 1"));

        provider.clear();

        Assert.isTrue(provider.toString().equals(""));
    }

}
