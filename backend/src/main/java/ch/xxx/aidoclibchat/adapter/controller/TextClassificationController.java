/**
 *    Copyright 2023 Sven Loesekann
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package ch.xxx.aidoclibchat.adapter.controller;

import ch.xxx.aidoclibchat.domain.model.dto.ClassificationRow;
import ch.xxx.aidoclibchat.domain.model.dto.ClassificationSummary;
import ch.xxx.aidoclibchat.usecase.service.TextClassificationService;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.MappingIterator;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("rest/text-classification")
public class TextClassificationController {
    private final TextClassificationService textClassificationService;
    private final CsvMapper mapper = new CsvMapper();

    public TextClassificationController(TextClassificationService textClassificationService) {
        this.textClassificationService = textClassificationService;
    }

    @PostMapping("/classify")
    public ClassificationSummary classify(@RequestParam("file") MultipartFile archive) {
        var rows = createClassificationRows(archive);
        return this.textClassificationService.classify(rows);
    }

    private @NonNull ArrayList<ClassificationRow> createClassificationRows(MultipartFile archive) {
        var rows = new ArrayList<ClassificationRow>();
        try(var inputStream = extractInputStream(archive)) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            try (MappingIterator<ClassificationRow> it = mapper
                    .readerFor(ClassificationRow.class).with(schema).readValues(inputStream)) {
                rows.addAll(it.readAll());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    private static InputStream extractInputStream(MultipartFile archive) throws IOException {
        var inputStream = new ByteArrayInputStream(new byte[0]);
        try(var outputStream = new ByteArrayOutputStream()) {
            try (var zis = new ZipInputStream(archive.getInputStream())) {
                byte[] buffer = new byte[4096];
                for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                    if (ze.isDirectory()) {
                        continue;
                    } else if (ze.getName().endsWith(".csv")) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                    }
                }
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            }
        }
        return inputStream;
    }
}
