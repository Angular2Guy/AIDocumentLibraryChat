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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("rest/text-classification")
public class TextClassificationController {


    @PostMapping("/classify")
    public long classify(@RequestParam("file") MultipartFile archive) {
        var csv = extractCsvString(archive);

        return 0;
    }

    private static String extractCsvString(MultipartFile archive) {
        String csv = "";
        try {
            var zis = new ZipInputStream(archive.getInputStream());
            byte[] buffer = new byte[4096];
            StringBuilder sb = new StringBuilder();
            for(ZipEntry ze = zis.getNextEntry();ze != null;ze = zis.getNextEntry()) {
                if(ze.isDirectory()) {
                    continue;
                } else if(ze.getName().endsWith(".zip")) {
                    int len;
                    while((len = zis.read(buffer)) > 0) {
                        sb = sb.append(new String(buffer, 0, len));
                    }

                }
            }
            csv = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return csv;
    }
}
