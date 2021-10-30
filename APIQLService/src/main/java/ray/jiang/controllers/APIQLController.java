package ray.jiang.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/apiql")
public class APIQLController {

    @PostMapping("/query")
    public String query(@RequestParam Map<String,String> params) {

        return "欢迎使用APIQL服务";
    }
}
