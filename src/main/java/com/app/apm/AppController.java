package com.app.apm;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@RestController
public class Trace extends HttpServlet {

  @Autowired
  private ApplicationContext context;

  private static final Tracer tracer = Tracing.getTracer();

  static {
    try {
      System.out.println("Init StackdriverTraceExporter");
      StackdriverTraceExporter.createAndRegister(
        StackdriverTraceConfiguration.builder()
        .setProjectId("student2-282707")
        .build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping("/trace")
  public static String trace() {

    try (Scope ignored = tracer.spanBuilder("SpanTrace").setSampler(Samplers.alwaysSample()).startScopedSpan()) {
      tracer.getCurrentSpan().addAnnotation("annotation example");

        String input = "ReverseOfString";
        reverse(input);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      System.out.println("annotation created");
      return "abc";
    }


  @RequestMapping("/profile")
  public String profile() {

    try (Scope ss = tracer.spanBuilder("MyChildrenWorkSpan").startScopedSpan()) {
      System.out.print("abc");
      //  System.out.print(item.toJSONPretty());
      BigInteger fact = BigInteger.valueOf(1);
      for (int i = 1; i <= 10; i++) {
        fact = fact.multiply(BigInteger.valueOf(i));
      }
      System.out.println(fact);
      tracer.getCurrentSpan().addAnnotation("Finished initial work");

      //System.out.print(fact);

    } catch (RuntimeException e) {

      /* Add the Segment add exception here for post method */
      System.err.println("Something went wrong");
      //  System.err.println(e.getMessage());

    } finally {
      /* Add the end segment code here for post method */

    }
    return "segment";
  }

  @RequestMapping("/shut")
  public String close() {
    SpringApplication.exit(context);
    return "abcf";
  }

  public static void reverse(String str) throws InterruptedException{
        char[] ch = str.toCharArray();
        Thread.sleep(1000);

        for (int i = ch.length - 1; i >= 0; i--)
        {
          System.out.print(ch[i]);
        }
          //return "Reverse Execute";

  }

}
