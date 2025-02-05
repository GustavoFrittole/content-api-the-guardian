package com.apitheguardian;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.apitheguardian.bean.Response;
import com.apitheguardian.bean.ResponseWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

public class GuardianContentApi {

  static {
// Only one time
    Unirest.setObjectMapper(new ObjectMapper() {
      private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
          = new com.fasterxml.jackson.databind.ObjectMapper();

      public <T> T readValue(String value, Class<T> valueType) {
        try {
          return jacksonObjectMapper.readValue(value, valueType);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      public String writeValue(Object value) {
        try {
          return jacksonObjectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  private final static String TARGET_URL = "http://content.guardianapis.com/search";
  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private final String apiKey;
  private String section;
  private String tag;
  private Date toDate;
  private Date fromDate;

  //variabile aggiunta
  private String order;

  //variabile aggiunta
  private int pageSize;

  public GuardianContentApi(final String apiKey) {
    this.apiKey = apiKey;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public void setFromDate(Date date) {
    this.fromDate = date;
  }

  public void setToDate(Date date) {
    this.toDate = date;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  //metodo aggiunto
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  //metodo aggiunto
  public void setOrder(String order) {
    this.order = order;
  }

  //metodo modificato
  public Response getContent(String query) {
    return getContent(query, 1);
  }

  //metodo modificato
  public Response getContent() {
    return getContent(null, 1);
  }

  //metodo modificato
  public Response getContent(String query, int page) {

    HttpRequest request = Unirest.get(TARGET_URL)
            .queryString("api-key", apiKey)
            .header("accept", "application/json");

    //sezione aggiunta
    if (pageSize > 1 && pageSize < 201) {
      request.queryString("page-size", pageSize);
    }
    //sezione aggiunta
    if (page > 0) {
      request.queryString("page", page);
    }
    if (query != null && !query.isEmpty()) {
      request.queryString("q", query);
    }
    //sezione aggiunta
    if (order != null && !order.isEmpty()) {
      request.queryString("order-by", order);
    }

    if (section != null && !section.isEmpty()) {
      request.queryString("section", section);
    }

    if (tag != null && !tag.isEmpty()) {
      request.queryString("tag", tag);
    }

    if (fromDate != null) {
      request.queryString("from-date", dateFormat.format(fromDate));
    }
    if (toDate != null) {
      request.queryString("to-date", dateFormat.format(toDate));
    }

    request.queryString("show-fields", "all");

    HttpResponse<ResponseWrapper> response = null;
    try {
      response = request.asObject(ResponseWrapper.class);
    } catch (UnirestException e) {
      throw new RuntimeException(e);
    }
    return response.getBody().getResponse();

  }
}
