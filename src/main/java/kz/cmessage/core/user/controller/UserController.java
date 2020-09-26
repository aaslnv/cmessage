package kz.cmessage.core.user.controller;

import kz.cmessage.core.user.dto.UpdateUserByManagerRequestDto;
import kz.cmessage.core.user.dto.UpdateUserRequestDto;
import kz.cmessage.core.user.dto.UserDto;
import kz.cmessage.core.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getByUsername(@RequestParam String username) {
        return userService.getByUsername(username)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // FIXME: 03.09.2020 Add hasAuthority('manager') annotation
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok().body(userService.update(id, userDto));
    }

    // FIXME: 03.09.2020 Add hasAuthority('manager') annotation
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    // FIXME: 03.09.2020 Add hasAuthority('manager') annotation
    @PatchMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> updateFieldsByManager(@PathVariable Long id, @RequestParam UpdateUserByManagerRequestDto dto) {
        return ResponseEntity.ok().body(userService.updateFields(id, dto));
    }

    @PatchMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> updateFields(@PathVariable Long id,
                                          @RequestParam UpdateUserRequestDto dto) {
        return ResponseEntity.ok().body(userService.updateFields(id, dto));
    }
}
