package com.azbackend.azbackend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class Controller {


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/")
    public String aabb() {
        return "ECHOing";
    }

    @GetMapping("/get")
    public List<String> aa() {
        List<String> a = new ArrayList<>();
        namedParameterJdbcTemplate.query("select * from blog b where title ='AZ_204_TEST'", (e) -> {
            String mail = e.getString("content");
            a.add(mail);
        });
        return a;
    }

    @PostMapping("/insert")
    public void insert(@RequestBody String content) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", content);
        namedParameterJdbcTemplate.update("insert into blog (userid,title,content) values (1,'AZ_204_TEST',:content)", params);

    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam MultipartFile file) {
        try {
            file.transferTo(new File("E:\\azure\\204-proj\\file.png"));
        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.ok().build();
    }

}
