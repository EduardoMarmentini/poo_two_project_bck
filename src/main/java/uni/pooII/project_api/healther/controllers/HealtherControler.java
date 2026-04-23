package uni.pooII.project_api.healther.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/healther")
public class HealtherControler {
    
    @GetMapping
    public Map<String, String> getHealther() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Calma pai, ta de pé!");
        return response;
    }
}
