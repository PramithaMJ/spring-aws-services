package com.pmj.s3.dto;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDto {
    private long id;
    private String name;
    private String email;
    private String jobTitle;
    private String imageUrl;
}
