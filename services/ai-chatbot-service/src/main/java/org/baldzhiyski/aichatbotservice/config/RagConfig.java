package org.baldzhiyski.aichatbotservice.config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Locale;

@Configuration
public class RagConfig {

    private static final Logger log = LogManager.getLogger(RagConfig.class);

    @Value("${spring.application.vector.schema:public}")
    private String schema;

    @Value("${spring.application.vector.table:ai_documents}")
    private String table;

    // Accept COSINE | EUCLIDEAN | DOT (case-insensitive)
    @Value("${spring.application.vector.distance:COSINE}")
    private String distance;


    @Value("${spring.application.vector.dimensions:1536}")
    private int dimensions;

    @Value("${spring.application.vector.ingestOnBoot:true}")
    private boolean ingestOnBoot;

    @Value("classpath:/data/shop-policies.txt")
    private Resource policies;

    @Bean
    public PgVectorStore pgVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel
    ) {
        // Normalize props
        String schemaLc = schema.toLowerCase(Locale.ROOT);
        String tableLc  = table.toLowerCase(Locale.ROOT);

        PgVectorStore.PgDistanceType distanceType = switch (distance.toUpperCase(Locale.ROOT)) {
            case "EUCLIDEAN", "L2" -> PgVectorStore.PgDistanceType.EUCLIDEAN_DISTANCE;
            case "DOT", "IP"       -> PgVectorStore.PgDistanceType.NEGATIVE_INNER_PRODUCT;
            default                -> PgVectorStore.PgDistanceType.COSINE_DISTANCE;
        };


        PgVectorStore store = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .schemaName(schemaLc)
                .vectorTableName(tableLc)
                .dimensions(dimensions)                // IMPORTANT: must match your embedding model
                .distanceType(distanceType)            // defaults to COSINE if omitted
                .initializeSchema(true)                // create table/index if missing
                .build();

        if (ingestOnBoot && tableIsEmpty(jdbcTemplate, schemaLc, tableLc)) {
            ingestPolicies(store);
        }
        return store;
    }

    private boolean tableIsEmpty(JdbcTemplate jdbcTemplate, String schemaLc, String tableLc) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + schemaLc + "." + tableLc, Integer.class);
            return count == null || count == 0;
        } catch (Exception e) {
            log.info("Vector table {}.{} not found yet; will create and ingest.", schemaLc, tableLc);
            return true;
        }
    }

    private void ingestPolicies(VectorStore store) {
        try {
            log.info("Ingesting policies from {}", policies.getDescription());
            var reader = new TextReader(policies);
            reader.getCustomMetadata().put("source", "policies/shop-policies.txt");

            List<Document> docs = reader.get();
            TextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(docs);

            store.add(chunks);
            log.info("Ingested {} policy chunks into pgvector store.", chunks.size());
        } catch (Exception e) {
            log.error("Failed to ingest policies", e);
        }
    }
}
