package com.arms.api.jira.user.controller;

import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.jira.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{connectId}")
    public ResponseEntity<List<UserDTO>> findAllUsers(@PathVariable("connectId") String connectId) throws Exception {

        List<UserDTO> users = userService.findAllUsers(connectId);

        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsersByConnectIds(@RequestParam("connectIds") List<String> connectIds) throws Exception {

        List<UserDTO> users = userService.findAllUsersByConnectIds(connectIds);

        return ResponseEntity.ok(users);
    }
}
