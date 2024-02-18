package com.alterdekim.game.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "textdata_val")
public class TextDataVal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 65555)
    @NotNull
    private String textEng;

    @Column(length = 65555)
    @NotNull
    private String textRus;

    public TextDataVal(String textEng, String textRus) {
        this.textEng = textEng;
        this.textRus = textRus;
    }
}
