package pw.mssql.wineadvisor.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.mssql.wineadvisor.model.Wine;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/wine")
public class WineController {

    @POST
    @RequestMapping("/ask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Wine ask(@RequestBody Wine question) {

        System.out.println(question.toString());
        Wine answer = new Wine();
        answer.setPurpose("Hello world!");
        return answer;
    }
}
