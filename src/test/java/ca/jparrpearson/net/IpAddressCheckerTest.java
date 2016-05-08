package ca.jparrpearson.net;

import org.junit.Assert;
import org.junit.Test;

public class IpAddressCheckerTest {

    private IpAddressChecker checker = new IpAddressChecker();

    @Test
    public void testGetAddress() {
        String address = checker.getAddress();
        System.out.println("IP address: " + address);
        // Assumes IPv4
        Assert.assertTrue(!address.isEmpty()
                && address.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"));
    }

}
