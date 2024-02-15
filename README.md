
# Multi-tenancy example

This example is built on https://github.com/camunda-community-hub/camunda-8-process-solution-template (which may explain some additional files)

## Programmatic
ZeebeClient can be used with the `tenant` method, to specify the tenant that should be used for the given command, like this:

```
        DeploymentEvent depl =
            zeebeClient
                .newDeployResourceCommand()
                .addResourceFile(
                    "/hidden/absolute/path/src/main/resources/models/camunda-process.bpmn")
                .tenantId("<default>")
//                .tenantId("test-tenant")
                .send()
                .join();
```


## application.yaml
Configuration of default tenant for JobWorker and spring-managed Zeebe Client:
```
#multi-tenant related
zeebe.client.default-tenant-id: <default>
zeebe.client.default-job-worker-tenant-ids:
  - <default>
```

Configurable tenant-ids per JobWorker have been merged into spring-zeebe (https://github.com/camunda-community-hub/spring-zeebe/commit/e78ae40dcca1bf7b8b6394e3361228a5cf4afa06) and will presumably be part of the next Release.

