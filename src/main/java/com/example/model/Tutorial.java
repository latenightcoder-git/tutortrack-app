package com.example.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Tutorial {
    private int id;
    private String title;
    private String author;
    private String url;
    private LocalDate publishedDate;

    public Tutorial(String title, String author, String url, LocalDate publishedDate) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.publishedDate = publishedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tutorial tutorial = (Tutorial) o;
        return id == tutorial.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
