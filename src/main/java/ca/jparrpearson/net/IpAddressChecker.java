package ca.jparrpearson.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Determines the external IP address by looking it up from an online service,
 * then saves it to a local file. It will send a notification if it changes from
 * the last time it checked.
 * 
 * @author Jeremy Parr-Pearson
 *
 */
public class IpAddressChecker {

    private static final String PROPERTIES_FILE = "conf/ip-address-checker.properties";

    private static PropertiesConfiguration properties = new PropertiesConfiguration();

    public IpAddressChecker() {
        loadProperties();
    }

    private void loadProperties() {
        try {
            properties = new PropertiesConfiguration(PROPERTIES_FILE);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load the properties file.", e);
        }
    }

    /**
     * Gets the external IP address by using the configured services.
     * 
     * @return The external IP address, or null if it could not be obtained.
     */
    public String getAddress() {
        String address = null;
        for (Object service : properties.getList("services")) {
            address = getAddress((String) service);
            if (address != null) {
                break;
            }
        }
        return address;
    }

    /**
     * Gets the external IP address by using the specified service address.
     * 
     * @param service
     *            The URL of the service to query.
     * @return The external IP address, or null if it could not be obtained.
     */
    private String getAddress(String service) {
        String address = null;
        try {
            URL url = new URL(service);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            address = in.readLine().trim();
        } catch (IOException e) {
            // Ignore exceptions
        }
        return address;
    }

    /**
     * Checks if the given address is different from the one in the properties
     * file.
     * 
     * @param address
     *            The IP address to check against the last value.
     * @return True if the address is different, false if it is the same.
     */
    public boolean hasChanged(String address) {
        String lastAddress = properties.getString("last.address");
        return lastAddress == null || !(lastAddress).equals(address);
    }

    /**
     * Saves the given address to the properties file on disk.
     * 
     * @address The IP address to save.
     */
    public void saveAddress(String address) {
        try {
            properties.setProperty("last.address", address);
            properties.save();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to save the properties file.", e);
        }
    }

    /**
     * Sends the given address to the configured email address. Uses the
     * configured SMTP options to send the email.
     * 
     * @address The IP address to send.
     */
    public void sendAddress(String address) {
        try {
            String host = properties.getString("mail.smtp.host");
            int port = properties.getInt("mail.smtp.port");
            String username = properties.getString("mail.smtp.username");
            String password = properties.getString("mail.smtp.password");
            String to = properties.getString("mail.to");
            String from = properties.getString("mail.from");
            String subject = properties.getString("mail.subject");

            Email email = new SimpleEmail();
            email.setHostName(host);
            email.setSmtpPort(port);
            email.setAuthentication(username, password);
            email.setSSLOnConnect(true);
            email.setStartTLSEnabled(true);
            email.addTo(to);
            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(address);
            email.send();
        } catch (EmailException e) {
            throw new RuntimeException("Unable to send the email.", e);
        }
    }

    public static void main(String[] args) {
        IpAddressChecker checker = new IpAddressChecker();
        String address = checker.getAddress();
        System.out.println("IP address: " + address);
        // Only update the IP address and email it if it has changed
        if (checker.hasChanged(address)) {
            checker.saveAddress(address);
            System.out.println("IP address updated in properties file.");
            boolean sendMail = properties.getBoolean("email.enabled", true);
            if (sendMail) {
                checker.sendAddress(address);
                System.out.println("IP address sent to email.");
            }
        }
    }

}
