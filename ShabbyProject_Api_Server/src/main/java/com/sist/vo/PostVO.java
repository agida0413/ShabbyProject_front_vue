package com.sist.vo;

import java.sql.Date;

import lombok.Data;
import lombok.Getter;
@Getter
public class PostVO {
private int postNum;
private int idNum;
private String content;
private Date createDate;
private Date editDate;
private String canReply;
private String onlyMe;

}
