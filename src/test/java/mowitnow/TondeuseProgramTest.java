package mowitnow;

import mowitnow.infra.RealTondeuseProgram;
import mowitnow.infra.SimplePelouse;
import mowitnow.infra.SimpleTondeuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static mowitnow.TondeuseCommand.A;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TondeuseProgramTest {

    private TondeuseProgram programmeTondeuse;

    @BeforeEach
    public void setUp() {
        programmeTondeuse = new RealTondeuseProgram();
    }

    @ParameterizedTest
    @DisplayName("« D » et « G » font pivoter la tondeuse de 90° à droite ou à gauche respectivement, sans la déplacer.")
    @CsvSource(delimiter = '|', useHeadersInDisplayName = true, textBlock = """
              command | tondeuse orientation | expected new orientation
                    D |                   N  |                        E
                    D |                   E  |                        S
                    D |                   S  |                        W
                    D |                   W  |                        N
                    G |                   S  |                        E
                    G |                   W  |                        S
                    G |                   N  |                        W
                    G |                   E  |                        N
            """) // Given (and expected)
    public void should_rotate_at_90_degree_for_d_or_g_right_or_left_without_moving_it(
            TondeuseCommand tondeuseCommand,
            Orientation tondeuseOrientation,
            Orientation expectedOrientation) {
        //Given (again)
        final Integer dummyX = 1;
        final Integer dummyY = 1;
        final Integer dummyMaxX = dummyX + 2;
        final Integer dummyMaxY = dummyY + 2;
        final Pelouse dummyPelouse = new SimplePelouse(dummyMaxX, dummyMaxY);
        final Tondeuse dummyTondeuse = new SimpleTondeuse(dummyX, dummyY, tondeuseOrientation);

        //When
        final Tondeuse tondeuseControledByDOrG =
                programmeTondeuse.control(dummyTondeuse, List.of(tondeuseCommand), dummyPelouse);

        //Then
        assertEquals(expectedOrientation, tondeuseControledByDOrG.getOrientation()); // ie. "pivoter"
        assertingNoMovement(dummyX, dummyY, tondeuseControledByDOrG); // ie. "sans la déplacer"
    }

    private void assertingNoMovement(final Integer fromX, final Integer fromY, final Tondeuse fromTondeuse) {
        assertEquals(fromX, fromTondeuse.getX());
        assertEquals(fromY, fromTondeuse.getY());
    }

    private void assertingNoRotation(final Orientation fromOrientation, final Tondeuse fromTondeuse) {
        assertEquals(fromOrientation, fromTondeuse.getOrientation());
    }

    @ParameterizedTest
    @DisplayName("« A » signifie que l'on avance la tondeuse d'une case dans la direction à laquelle elle fait face, et sans modifier son orientation.")
    @CsvSource(delimiter = '|', useHeadersInDisplayName = true, textBlock = """
                    tondeuse orientation |      tondeuse starting X | tondeuse starting Y | tondeuse expected X | tondeuse expected Y
                                       N |                        1 |                   1 |                   1 |                   2
                                       E |                        1 |                   1 |                   2 |                   1
                                       S |                        1 |                   1 |                   1 |                   0
                                       W |                        1 |                   1 |                   0 |                   1
            """) // Given (and expected)
    public void should_move_forward_of_one_case_towards_facing_direction_without_modifying_direction(
            Orientation tondeuseOrientation,
            int startingX,
            int startingY,
            int expectedX,
            int expectedY
            ) {
        //Given
        final Integer dummyMaxX = startingX + 2;
        final Integer dummyMaxY = startingY + 2;
        final Tondeuse dummyTondeuse = new SimpleTondeuse(startingX, startingY, tondeuseOrientation);
        final Pelouse dummyPelouse = new SimplePelouse(dummyMaxX, dummyMaxY);

        //When
        final Tondeuse tondeuseControledByA = programmeTondeuse.control(dummyTondeuse, List.of(A), dummyPelouse);

        //Then
        assertingNoRotation(tondeuseOrientation, tondeuseControledByA);
        assertEquals(expectedX, tondeuseControledByA.getX());
        assertEquals(expectedY, tondeuseControledByA.getY());
    }


    @ParameterizedTest
    @DisplayName("Si la position après mouvement est en dehors de la pelouse, la tondeuse ne bouge pas, conserve son orientation et traite la commande suivante.")
    @CsvSource(delimiter = '|', useHeadersInDisplayName = true, textBlock = """
                    tondeuse orientation |      tondeuse starting X | tondeuse starting Y | pelouse max X | pelouse max Y
                                       N |                        0 |                   3 |             3 |             3
                                       N |                        3 |                   3 |             3 |             3
                                       E |                        3 |                   0 |             3 |             3
                                       E |                        3 |                   3 |             3 |             3
                                       S |                        0 |                   0 |             3 |             3
                                       S |                        3 |                   0 |             3 |             3
                                       W |                        0 |                   0 |             3 |             3
                                       W |                        0 |                   3 |             3 |             3
            """)
    public void should_not_move_or_rotate_and_compute_next_command_when_position_outside_mow(
            Orientation tondeuseOrientation,
            int startingX,
            int startingY,
            int pelouseMaxX,
            int pelouseMaxY
    ) {
        //Given
        final Tondeuse bordelineTondeuse = new SimpleTondeuse(startingX, startingY, tondeuseOrientation);
        final Pelouse dummyPelouse = new SimplePelouse(pelouseMaxX, pelouseMaxY);

        //When
        final Tondeuse tondeuseControlledByA = programmeTondeuse.control(bordelineTondeuse, List.of(A, A), dummyPelouse);

        //Then
        assertingNoRotation(tondeuseOrientation, tondeuseControlledByA);
        assertingNoMovement(startingX, startingY, tondeuseControlledByA);
    }
    @Test
    @DisplayName("Pour programmer la tondeuse, on lui fournit un fichier d'entrée construit... NB: Les" +
            "données en entrée sont injectées sous forme de fichDumbier.")
    public void should_give_tondeuses_position_data_for_file_content() {
        //Given
        final File fichierDentreePath = new File("src/test/resources/FichierDentree");

        //When
        final List<Tondeuse> tondeuseControlledByFile = programmeTondeuse.control(fichierDentreePath);

        //Then
        assertEquals(
                "1 3 N 5 1 E",
                tondeuseControlledByFile
                        .stream()
                        .map(t -> String.format("%d %d %s", t.getX(), t.getY(), t.getOrientation()))
                        .collect(Collectors.joining(" "))
        );
    }
}

