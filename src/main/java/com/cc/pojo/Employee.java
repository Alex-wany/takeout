package com.cc.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value="id",type=IdType.ASSIGN_ID)//指定自增策略 ASSIGN_ID是雪花算法 ID_WORKER是默认的自增策略 AUTO是自动增长 NONE是无
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)//插入时填充字段，后面的是枚举
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入、更新时填充字段，后面的是枚举
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入、更新时填充字段，后面的是枚举
    private Long updateUser;


}
