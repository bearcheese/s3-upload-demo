package com.epam.vercm2.upload.demo;

import com.amazonaws.util.BinaryUtils;
import com.epam.vercm2.upload.demo.model.Policy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static com.epam.vercm2.upload.demo.model.ExactValueCondition.exactValueCondition;
import static com.epam.vercm2.upload.demo.model.MatchType.STARTS_WITH;
import static com.epam.vercm2.upload.demo.model.MatcherCondition.matcherCondition;
import static java.time.temporal.ChronoField.*;
import static org.apache.commons.codec.Charsets.UTF_8;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    private static final String SCHEME = "AWS4";
    private static final String AWS_SERVICE_NAME = "s3";
    private static final String TERMINATOR = "aws4_request";
    private static final String HMAC_SHA_256 = "HmacSHA256";

    @Value("${aws.accesskey}")
    private String awsAccessKey;

    @Value("${aws.secret_key}")
    private String awsSecretKey;

    @Value("${aws.region_name}")
    private String regionName;

    @Value("${aws.bucket_name}")
    private String bucketName;

    @Value("${policy.validity_in_seconds}")
    private long validityInSeconds;

    @Value("${success_action_redirect}")
    private String successActionRedirect;

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {
        ZonedDateTime currentTime = LocalDateTime.now().atZone(UTC);
        String credential = generateCredential(currentTime);
        String dateString = currentTime.with(LocalTime.MIN).format(BASIC_ISO_DATETIME);
        String endpointUrl = generateEndpointUrl();

        Policy policy = generatePolicy(currentTime, validityInSeconds, credential);
        String rawPolicy = objectMapper.writeValueAsString(policy);
        LOGGER.info("Raw policy: {}", rawPolicy);
        String base64EncodedPolicy = BinaryUtils.toBase64(rawPolicy.getBytes(UTF_8));
        byte[] signature = signBase64EncodedPolicy(currentTime, base64EncodedPolicy);

        model.addAttribute("endpointUrl", endpointUrl);
        model.addAttribute("keyPrefix", RandomStringUtils.randomAlphanumeric(6));
        model.addAttribute("credential", credential);
        model.addAttribute("date", dateString);
        model.addAttribute("policy", base64EncodedPolicy);
        model.addAttribute("signature", BinaryUtils.toHex(signature));

        return "index";
    }

    private String generateEndpointUrl() {
        String endpointUrl;
        if (regionName.equals("us-east-1")) {
            endpointUrl = "https://s3.amazonaws.com/" + bucketName;
        } else {
            endpointUrl = "https://s3-" + regionName + ".amazonaws.com/" + bucketName;
        }
        return endpointUrl;
    }

    private byte[] signBase64EncodedPolicy(ZonedDateTime currentTime, String base64EncodedPolicy) {
        // compute the signing key
        byte[] kSecret = (SCHEME + awsSecretKey).getBytes();
        byte[] kDate = sign(currentTime.format(BASIC_ISO_DATE), kSecret);
        byte[] kRegion = sign(regionName, kDate);
        byte[] kService = sign(AWS_SERVICE_NAME, kRegion);
        byte[] kSigning = sign(TERMINATOR, kService);
        return sign(base64EncodedPolicy, kSigning);
    }

    private String generateCredential(ZonedDateTime currentTime) {
        return awsAccessKey + "/" + currentTime.format(BASIC_ISO_DATE) + "/" + regionName + "/" + AWS_SERVICE_NAME + "/" + TERMINATOR;
    }

    private Policy generatePolicy(ZonedDateTime issuedAt, long validityInSeconds, String credential) {
        Policy policy = new Policy();

        ZonedDateTime expiresAt = issuedAt.plusSeconds(validityInSeconds);
        String dateString = issuedAt.with(LocalTime.MIN).format(BASIC_ISO_DATETIME);

        policy.setExpiration(expiresAt);
        policy.addCondition(exactValueCondition("bucket", bucketName));
        policy.addCondition(matcherCondition(STARTS_WITH, "$key", ""));
        policy.addCondition(exactValueCondition("success_action_redirect", successActionRedirect));
        policy.addCondition(matcherCondition(STARTS_WITH, "$x-amz-meta-tag", ""));
        policy.addCondition(exactValueCondition("x-amz-credential", credential));
        policy.addCondition(exactValueCondition("x-amz-algorithm", "AWS4-HMAC-SHA256"));
        policy.addCondition(exactValueCondition("x-amz-date", dateString));
        return policy;
    }

    private static byte[] sign(String stringData, byte[] key) {
        try {
            byte[] data = stringData.getBytes(UTF_8);
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(key, HMAC_SHA_256));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

    private static final DateTimeFormatter BASIC_ISO_DATETIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral("T")
            .appendValue(HOUR_OF_DAY, 2)
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendOffset("+HHMMss", "Z")
            .toFormatter();

    private static final DateTimeFormatter BASIC_ISO_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();

}
