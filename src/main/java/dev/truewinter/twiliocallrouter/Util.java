package dev.truewinter.twiliocallrouter;

import dev.truewinter.twiliocallrouter.config.Config;
import io.javalin.http.Context;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Pattern;

public class Util {
    // https://stackoverflow.com/a/15954821
    public static Path getInstallPath() {
        Path relative = Paths.get("");
        return relative.toAbsolutePath();
    }

    public static String getVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(Util.class.getClassLoader().getResourceAsStream("twiliocallrouter.properties"));
        return properties.getProperty("version");
    }

    public static String extractNumberFromSipUrl(String sipUrl) {
        if (!sipUrl.startsWith("sip:")) {
            System.out.println("Cannot extract E164 from \"" + sipUrl + "\". Not a SIP URL, skipping.");
            return sipUrl;
        }

        // sip:2027621401@example.sip.twilio.com;user=phone -> 2027621401 (US)
        return sipUrl.replace("sip:", "").split(Pattern.quote("@"))[0];
    }

    // TODO: This method isn't entirely compatible with toll-free numbers, nor short numbers (such as emergency numbers)
    public static String extractE164FromSipUrl(String defaultCountryCode, String sipUrl) {
        if (!sipUrl.startsWith("sip:")) {
            System.out.println("Cannot extract E164 from \"" + sipUrl + "\". Not a SIP URL, skipping.");
            return sipUrl;
        }

        // sip:2027621401@example.sip.twilio.com;user=phone -> 2027621401 (US)
        String number = sipUrl.replace("sip:", "").split(Pattern.quote("@"))[0];

        // 0012027621401 -> +12027621401 (US)
        if (number.startsWith("00")) {
            number = number.replaceAll("^00", "+");
        }

        // 2027621401 -> +12027621401 (US)
        if (!number.startsWith("+")) {
            // 02079460123 -> 2079460123 (UK)
            if (number.startsWith("0")) {
                number = number.replaceAll("^0", "");
            }

            number = defaultCountryCode + number;
        }

        return number;
    }

    public static String getReferUrl(Context ctx, Config config) throws MalformedURLException {
        URL url = new URL(ctx.url());
        StringBuilder sb = new StringBuilder();

        if (config.isReferHttpsForced()) {
            sb.append("https");
        } else {
            sb.append(url.getProtocol());
        }
        sb.append("://");

        if (config.isAuthEnabled()) {
            sb.append(config.getUsername());
            sb.append(":");
            sb.append(config.getPassword());
            sb.append("@");
        }

        sb.append(ctx.host());
        sb.append("/refer");
        return sb.toString();
    }
}
