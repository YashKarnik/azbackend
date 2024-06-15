package com.azbackend.azbackend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/postgres")
@RequiredArgsConstructor
public class PostgresController {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/get")
    public List<String> get() {
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

}
