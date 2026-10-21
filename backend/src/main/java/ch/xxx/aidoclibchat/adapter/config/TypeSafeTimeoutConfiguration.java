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
package ch.xxx.aidoclibchat.adapter.config;

import org.springaicommunity.typesafe.TypeSafeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class TypeSafeTimeoutConfiguration {
    @Value("${spring.ai.typesafe.base-url:http://localhost:11434}")
    private String baseUrl;
    @Value("${spring.ai.typesafe.api-key:ollama-local-dummy}")
    private String apiKey;
    @Value("${spring.ai.typesafe.model:clef-flash:latest}")
    private String defaultModel;

    @Bean
    public TypeSafeClient typeSafeClient() {
        return TypeSafeClient.builder()
                .baseUrl(this.baseUrl)
                .apiKey(this.apiKey)
                .defaultModel(this.defaultModel)
                // Setzt das Timeout direkt über die Builder-Methode des Starters
                .timeout(Duration.ofSeconds(120))
                .build();
    }
}

