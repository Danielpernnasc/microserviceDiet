package com.trainday.microserviceDiet.infra.Seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainday.microserviceDiet.Repository.DietTemplateRepository;
import com.trainday.microserviceDiet.models.Diet;
import org.springframework.boot.CommandLineRunner;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class TemplateSeed  implements CommandLineRunner {

    private final DietTemplateRepository repository;
    private final ObjectMapper objectMapper;

    public TemplateSeed(DietTemplateRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            saveFile("seed/TemplateSeed.json");
        }
    }

    protected void saveFile(String filePath) throws Exception {
            try (
                    InputStream is = getClass()
                            .getClassLoader()
                            .getResourceAsStream(filePath)){
                if(is == null){
                    throw new FileNotFoundException("File not found: " + filePath);
                }

                Diet template = objectMapper.readValue(is, Diet.class);
                repository.save(template);
            }

    }
}
