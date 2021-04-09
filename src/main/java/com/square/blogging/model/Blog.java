package com.square.blogging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Blog {
    public enum Status {DRAFT, PUBLISHED}

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    private String title;
    private String content;
    private LocalDate date;
    private Status status;
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;
}
