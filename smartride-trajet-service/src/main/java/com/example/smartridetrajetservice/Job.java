//TEST
package com.example.smartridetrajetservice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    private int id;
    private String service;
    private boolean etat;
}


