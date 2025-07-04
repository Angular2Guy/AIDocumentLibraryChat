
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ch.xxx.aidoclibchat.adapter.config.FunctionConfig;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sven
 */
@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

    @Bean
    public ToolCallbackProvider myTools(FunctionConfig functionConfig) {        
        return MethodToolCallbackProvider.builder().toolObjects(functionConfig).build();
    }
}
