package mowitnow;

import mowitnow.infra.SimpleTondeuse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TondeuseTest {

    private Tondeuse tondeuse;

    @ParameterizedTest
    @DisplayName("la position de la tondeuse peut être « 0, 0, N », ce qui signifie qu'elle se situe dans le coin inférieur gauche de la pelouse, et orientée vers le Nord")
    @CsvSource({"0, 0, N"}) //Given
    public void should_accept_position_0_0_N(Integer zeroX, Integer zeroY, Orientation orientationN) {
        //When
        ThrowingSupplier<Tondeuse> constructionOfTondeuse = () -> tondeuse = new SimpleTondeuse(zeroX,zeroY,orientationN);

        //Then
        assertAll(
                () -> assertDoesNotThrow(constructionOfTondeuse),
                () -> assertEquals(zeroX,        tondeuse.getX()),
                () -> assertEquals(zeroY,        tondeuse.getY()),
                () -> assertEquals(orientationN, tondeuse.getOrientation())
        );
    }
}
