package mtn.momo.contract.repayment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ViewControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ViewController controller = new ViewController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testRedirect_NonStaticResource() throws Exception {
        mockMvc.perform(get("/somepath"))
                .andExpect(forwardedUrl("/"));
    }

    @Test
    public void testRedirect_StaticResource_NotForwarded() throws Exception {
        // This test ensures that static resources are not forwarded
        mockMvc.perform(get("/images/logo.png"))
                .andExpect(result -> {
                    assert result.getResponse().getForwardedUrl() == null;
                });
    }
}
