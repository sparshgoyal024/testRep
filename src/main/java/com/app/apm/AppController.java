package com.app.cloudacademy;

import java.lang. * ;
import java.io. * ;
import java.util. * ;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;

@RestController
public class AppController {

  @Autowired
  private ApplicationContext context;

  private static final Tracer tracer = Tracing.getTracer();

  static {
    try {
      System.out.println("Trace Exporter Registered");
      StackdriverTraceExporter.createAndRegister(
      StackdriverTraceConfiguration.builder().build());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping("/trace")
  public static Tracer trace() throws InterruptedException {

    try (Scope ignored = tracer.spanBuilder("TraceSpan").setSampler(Samplers.alwaysSample()).startScopedSpan()) {
      tracer.getCurrentSpan().addAnnotation("Thread Sleep 1000ms Created");

      Thread.sleep(2000);

    } catch(Exception e) {
      throw new RuntimeException(e);
    }

    return tracer;
  }

  @RequestMapping("/profile")
  public String profile() {

    try {

      BigInteger fact = BigInteger.valueOf(1);
      for (int i = 1; i <= 10; i++) {
        fact = fact.multiply(BigInteger.valueOf(i));
      }

    } catch(RuntimeException e) {

      System.err.println("Something went wrong");

    }
    return "Profiler API Triggered";
  }

  @RequestMapping("/shut")
  public String close() {
    SpringApplication.exit(context);
    return "Application Shutting Down";
  }

}
