package com.epam.vercm2.upload.demo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.amazonaws.util.BinaryUtils;

@Controller
public class MainController {

    private static final String SCHEME = "AWS4";

    private static final String AWS_ACCESS_KEY = "xxx";
    private static final String AWS_SECRET_KEY = "xxx";
    private static final String AWS_REGION_NAME = "eu-central-1";
    private static final String AWS_SERVICE_NAME = "s3";
    private static final String TERMINATOR = "aws4_request";

    private static final String POLICY_TEMPLATE = "{ \n"
            + "  \"expiration\": \"%s\",\n"
            + "  \"conditions\": [\n"
            + "    {\"bucket\": \"formuploaddemobucket\"},\n"
            + "    [\"starts-with\", \"$key\", \"\"],\n"
            + "    {\"success_action_redirect\": \"http://localhost:8080\"},"
            + "    [\"starts-with\", \"$x-amz-meta-tag\", \"\"],\n"
            + "    {\"x-amz-credential\": \"%s\"},\n"
            + "    {\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"},\n"
            + "    {\"x-amz-date\": \"%s\" }\n"
            + "  ]\n"
            + "}";

    @GetMapping("/")
    public String index(Model model) throws UnsupportedEncodingException {

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiresAt = currentTime.plusMinutes(5);
        String credential = AWS_ACCESS_KEY + "/" + currentTime.format(DateTimeFormatter.BASIC_ISO_DATE) + "/" + AWS_REGION_NAME + "/" + AWS_SERVICE_NAME + "/" + TERMINATOR;
        model.addAttribute("keyPrefix", RandomStringUtils.randomAlphanumeric(6));
        model.addAttribute("credential", credential);
        String dateString = currentTime.format(DateTimeFormatter.BASIC_ISO_DATE) + "T000000Z";
        model.addAttribute("date", dateString);
        String rawPolicy = String.format(POLICY_TEMPLATE, expiresAt.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT),
                credential,
                dateString);
        System.out.println(rawPolicy);
        String base64EncodedPolicy = BinaryUtils.toBase64(rawPolicy
                .getBytes("UTF-8"));
        model.addAttribute("policy", base64EncodedPolicy);

        // compute the signing key
        byte[] kSecret = (SCHEME + AWS_SECRET_KEY).getBytes();
        byte[] kDate = sign(currentTime.format(DateTimeFormatter.BASIC_ISO_DATE), kSecret, "HmacSHA256");
        byte[] kRegion = sign(AWS_REGION_NAME, kDate, "HmacSHA256");
        byte[] kService = sign(AWS_SERVICE_NAME, kRegion, "HmacSHA256");
        byte[] kSigning = sign(TERMINATOR, kService, "HmacSHA256");
        byte[] signature = sign(base64EncodedPolicy, kSigning, "HmacSHA256");

        model.addAttribute("signature", BinaryUtils.toHex(signature));

        return "index";
    }

    private static byte[] sign(String stringData, byte[] key, String algorithm) {
        try {
            byte[] data = stringData.getBytes("UTF-8");
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

}
