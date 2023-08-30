package com.beetech.finalproject.web.dtos.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    // in case send mail to many users
    private String[] recipients;
}
