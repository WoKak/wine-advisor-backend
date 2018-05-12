package pw.mssql.wineadvisor.service;

import pw.mssql.wineadvisor.model.Wine;

import java.io.IOException;

public interface KnowledgeBaseService {

    String classifyWine(Wine wine) throws Exception;
    void moveLatestFileToMongoDB() throws IOException;
}
