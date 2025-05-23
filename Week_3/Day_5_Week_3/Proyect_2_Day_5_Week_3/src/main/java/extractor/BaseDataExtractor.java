// ================================
// 1. EXTRACTOR BASE Y ESPECIALIZADOS
// ================================

// BaseDataExtractor.java - Clase base para extractores
package extractor;


import exception.ETLException;
import model.Post;

import java.util.List;

public abstract class BaseDataExtractor {
    protected String sourceName;

    public BaseDataExtractor(String sourceName) {
        this.sourceName = sourceName;
    }

    public abstract List<Post> extractData() throws ETLException;
    public abstract void close();

    public String getSourceName() {
        return sourceName;
    }
}
