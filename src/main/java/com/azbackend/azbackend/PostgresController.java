package com.azbackend.azbackend;

import com.azbackend.azbackend.Pojos.Models.PostgresPostRequestModal;
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
    public List<PostgresPostRequestModal> get() {
        List<PostgresPostRequestModal> a = new ArrayList<>();
        namedParameterJdbcTemplate.query("select * from blog b where title ='AZ_204_TEST'", (e) -> {
            PostgresPostRequestModal postgresPostRequestModal = new PostgresPostRequestModal(e.getString("content"));
            a.add(postgresPostRequestModal);
        });
        return a;
    }

    @PostMapping("/insert")
    public void insert(@RequestBody PostgresPostRequestModal request) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", request.getContent());
        namedParameterJdbcTemplate.update("insert into blog (userid,title,content) values (1,'AZ_204_TEST',:content)", params);

    }

}
