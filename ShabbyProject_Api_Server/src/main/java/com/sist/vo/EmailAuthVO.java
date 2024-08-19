package com.sist.vo;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class EmailAuthVO {
private int emailAuthNum;
private String email;
private String code;
private LocalDateTime expiration;
private String isAuth;
}
