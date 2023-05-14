package gamein2.team.kernel.util;

import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestUtil {
    public static void sendSMS(String baseUrl, String token, String receptor, String template) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(baseUrl + "?receptor=%s&token=$s&template=$s",receptor,token,template);
        ResponseEntity<String> response = restTemplate.getForEntity(url,String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)){
            throw new Exception("خطا در ارسال پبامک");
        }
    }
}
