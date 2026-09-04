package reservation_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(properties = "app.security.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldProtectApiAndAcceptOnlyAccessTokens() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isUnauthorized());

        MvcResult registration = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Security",
                                  "lastName": "Tester",
                                  "email": "security.tester@example.com",
                                  "password": "secure-password-123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String response = registration.getResponse().getContentAsString();
        String accessToken = extractToken(response, "accessToken");
        String refreshToken = extractToken(response, "refreshToken");

        mockMvc.perform(get("/api/resources")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/resources")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }

        private String extractToken(String response, String fieldName) {
                Matcher matcher = Pattern.compile("\\\"" + fieldName + "\\\":\\\"([^\\\"]+)\\\"")
                                .matcher(response);

                if (!matcher.find()) {
                        throw new IllegalStateException("Missing " + fieldName + " in authentication response.");
                }

                return matcher.group(1);
        }
}
