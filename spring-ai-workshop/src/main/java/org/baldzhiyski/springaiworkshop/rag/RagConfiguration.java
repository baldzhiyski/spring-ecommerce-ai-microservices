package org.baldzhiyski.springaiworkshop.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class RagConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RagConfiguration.class);

    @Value("${spring.application.vectorStoreName}")
    private String vectorStoreName;

    @Value("${spring.application.vectorStoreDir}")
    private String vectorStoreDir;

    @Value("classpath:/data/models.json")
    private Resource models;

    @Bean
    public SimpleVectorStore simpleVectorStore(
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {

        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        Path path = Path.of(vectorStoreDir, vectorStoreName);
        ensureParentDirs(path);
        File file = path.toFile();

        if (file.exists()) {
            LOG.info("Loading vector store from {}", file.getAbsolutePath());
            store.load(file);
        } else {
            LOG.info("Vector store not found, creating new one at {}", file.getAbsolutePath());
            TextReader reader = new TextReader(models);
            reader.getCustomMetadata().put("filename", "models.json");
            List<Document> docs = reader.get();
            TextSplitter splitter = new TokenTextSplitter();
            store.add(splitter.apply(docs));
            store.save(file);
        }
        return store;
    }

    private static void ensureParentDirs(Path file) {
        Path parent = file.getParent();
        if (parent != null) {
            try { Files.createDirectories(parent); }
            catch (IOException e) { throw new UncheckedIOException("Failed to create dirs: " + parent, e); }
        }
    }
}
