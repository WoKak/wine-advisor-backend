package pw.mssql.wineadvisor.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.mssql.wineadvisor.model.Wine;
import pw.mssql.wineadvisor.service.KnowledgeBaseService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/wine")
public class WineController {

    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    public WineController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @POST
    @RequestMapping("/ask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Wine ask(@RequestBody Wine question) {

        Wine answer = new Wine();
        try {
            answer.setPurpose(knowledgeBaseService.classifyWine(question));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }

    @POST
    @RequestMapping("/refresh-base")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refresh() throws Exception {

        Process process = Runtime.getRuntime().exec("python mysql_to_arff.py");
        knowledgeBaseService.moveLatestFileToMongoDB();
        knowledgeBaseService.trainClassifier();

        return Response.ok().build();
    }
}
