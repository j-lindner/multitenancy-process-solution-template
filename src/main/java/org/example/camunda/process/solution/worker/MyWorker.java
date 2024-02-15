package org.example.camunda.process.solution.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyWorker {

  private static final Logger LOG = LoggerFactory.getLogger(MyWorker.class);

  @JobWorker
  public Map<String, Object> invokeMyService(@VariablesAsType Map<String, Object> variables) {
    LOG.info("Invoking myService with variables: " + variables);

    variables.put("Added from", "invokeMyService");

    return variables;
  }
}
