package com.hadaka_electro.internal.user.controller;

import com.hadaka_electro.common.entities.User;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.security.ElectroUserDetails;
import com.hadaka_electro.internal.user.UserDTO;
import com.hadaka_electro.internal.user.exportData.UserPdfExporter;
import com.hadaka_electro.internal.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<UserDTO>> listAllUsers(@RequestParam(required = false) String keyword,
                                                      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserDTO> users = userService.listAllUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/me")
    public ResponseEntity<Integer> getCurrentUser(@AuthenticationPrincipal ElectroUserDetails userDetails) {
        return ResponseEntity.ok(userService.findByEmail(userDetails.getUsername()).getId());
    }


    @PostMapping(value = "/save-user", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> saveUser(@RequestPart("user") @Valid UserDTO userDTO
            , @RequestPart("photo") MultipartFile userPhotoFile) throws IOException, DuplicatedObjectException {
        userService.saveUser(userDTO, userPhotoFile);

        return ResponseEntity.ok(Map.of(
                "message", "User saved successfully."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable int id) throws Exception {
        userService.deleteUser(id);
        Map<String, String> response = Map.of("message", "User with id " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-enable-status/{id}")
    public ResponseEntity<String> updateEnableStatus(@PathVariable int id) throws ObjectNotFoundException {

        boolean status = userService.updateEnableStatus(id);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body("User with id " + id + " has been enabled successfully");

        }
        return ResponseEntity.status(HttpStatus.OK).body("User with id " + id + " has been disabled successfully");
    }


    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPDF() {
        List<User> users = userService.findAllSorted();

        UserPdfExporter userPdfExporter = new UserPdfExporter();
        byte[] pdfBytes = userPdfExporter.export(users);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // make the browser downloads the file as users.pdf
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("Hadaka_Electro_Users.pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

}
