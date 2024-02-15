package org.example.camunda.process.solution;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath*:/models/*.*")
public class ProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProcessApplication.class, args);
  }

  @Component
  public static class Initializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

      String zeebeApi = "my-url:443";
      String oAuthAPI =
          "https://my-url:443/auth/realms/camunda-platform/protocol/openid-connect/token";
      String clientId = "zeebe";
      String clientSecret = "EasySecret";
      String audience = "[Zeebe Token Audience, e.g., zeebe.camunda.io]";

      OAuthCredentialsProvider credentialsProvider =
          new OAuthCredentialsProviderBuilder()
              .authorizationServerUrl(oAuthAPI)
              .audience(audience)
              .clientId(clientId)
              .clientSecret(clientSecret)
              .build();

      try (ZeebeClient zeebeClient =
          ZeebeClient.newClientBuilder()
              .gatewayAddress(zeebeApi)
              .credentialsProvider(credentialsProvider)
              .build()) {
        Topology join = zeebeClient.newTopologyRequest().send().join();
        System.out.println("Topology: " + join);

        // ============================
        // ====== start deploy ========
        // ============================
        System.out.println("Deploying to test-tenant");
        DeploymentEvent depl =
            zeebeClient
                .newDeployResourceCommand()
                .addResourceFile(
                    "/hidden/absolute/path/src/main/resources/models/camunda-process.bpmn")
                .tenantId("<default>")
//                .tenantId("test-tenant")
                .send()
                .join();
        System.out.println("Deployed to <default>: " + depl.getKey());
        // ============================
        // ====== end deploy ========
        // ============================

        // =====================================
        // ====== start create instance ========
        // =====================================
        ProcessInstanceEvent created =
            zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("camunda-process")
//                .bpmnProcessId("process_test-tenant")
                .latestVersion()
                .tenantId("<default>")
//                .tenantId("test-tenant")
                .send()
                .join();
        System.out.println("Created: " + created.getBpmnProcessId());
        // =====================================
        // ====== end create instance ========
        // =====================================
      }
    }
  }
}
