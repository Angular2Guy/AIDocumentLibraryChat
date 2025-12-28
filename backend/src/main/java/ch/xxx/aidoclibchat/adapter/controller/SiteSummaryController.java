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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.aidoclibchat.domain.model.dto.SiteSummaryDto;
import ch.xxx.aidoclibchat.usecase.service.SiteSummaryService;

@RestController
@RequestMapping("rest/site-summary")
public class SiteSummaryController {
    private final SiteSummaryService siteSummaryService;

    public SiteSummaryController(SiteSummaryService siteSummaryService) {
        this.siteSummaryService = siteSummaryService;
    }
    
    @GetMapping("/joke-about/{topic}")
    public SiteSummaryDto getJokeAboutTopic(@PathVariable String topic) {     
        return this.siteSummaryService.getJokeAboutTopic(topic);
    }
}