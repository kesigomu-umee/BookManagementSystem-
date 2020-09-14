package example.training.bookmanagement.presentation.controller;


import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public void fakeLogin(@ApiParam("User") @RequestParam String name, @ApiParam("Password") @RequestParam String password) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @PostMapping("/logout")
    public void fakeLogout() {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }
}
