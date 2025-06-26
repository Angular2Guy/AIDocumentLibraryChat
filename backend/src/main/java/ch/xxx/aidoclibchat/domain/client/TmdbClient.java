/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package ch.xxx.aidoclibchat.domain.client;

import java.util.function.Function;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 *
 * @author sven
 */
public interface TmdbClient extends Function<TmdbClient.Request, TmdbClient.Response> {

    record Request(@ToolParam(description = "The movie title") String query) {
    }

    record Response(String response) {
    }
}
