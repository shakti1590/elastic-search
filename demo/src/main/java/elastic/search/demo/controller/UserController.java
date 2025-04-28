package elastic.search.demo.controller;

import elastic.search.demo.model.User;
import elastic.search.demo.repository.UserRepository;
import elastic.search.demo.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String keyword) {
        return elasticsearchService.searchUsers(keyword);
    }

    @PostMapping
    public ResponseEntity <User> addUser(@RequestBody User user){
        User savedUser = userRepository.save(user);
        elasticsearchService.indexUser(savedUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}

