package ar.edu.itba.paw.models;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Difficulty {
    private final long difficultyId;
    private final String difficultyName;
}
