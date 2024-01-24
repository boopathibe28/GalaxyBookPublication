package com.galaxybookpublication.api;


public class Urls {
  //  public static final String BASE_URL = "https://staging.galaxybookpublication.in/"; //demo
    public static final String BASE_URL = "https://galaxybookpublication.in/"; //live

    public static final String CLIENT_LIST = "api/v1/client";
    public static final String PAYMENT_ORDER_LIST = "api/v2/client/{client_id}/payment";
    public static final String SPECIMEN_LIST = "api/v2/specimen";
    public static final String BOOK_LIST = "api/v2/book";
    public static final String DISTRICT_LIST = "api/v1/district";
    public static final String CLAIM_OPTIONS = "api/v2/claim/options";
    public static final String POST_CLAIM = "api/v2/claim";
    public static final String GET_APPOINTMENT = "api/v1/appointment";
    public static final String CLAIM_EDIT = "api/v2/claim/{id}";
    public static final String DELETE_CLAIM = "api/v2/claim/{id}";
    public static final String PAYMENT_PDF_DOWNLOAD = "api/v2/client/{uuid}/payment-export";


}
