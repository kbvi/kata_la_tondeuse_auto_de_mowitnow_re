package mowitnow;

/**
 * From specs "Pour contrôler la tondeuse, on lui envoie une séquence simple de lettres. Les lettres possibles sont « D », « G » et « A »"
 */
public enum TondeuseCommand {
    D,
    G,
    /**
     * From specs : "On présuppose que la case directement au Nord de la position (x, y) a pour coordonnées (x, y+1)"
     * this means that move forward with facing {@link Orientation} {@link Orientation#N N} or {@link Orientation#S S}
     * will respectively increase or decrease {@link Tondeuse#getY() y}.  But increase and decrease of
     * {@link Tondeuse#getX() x} can then be deduced, by elimination, to move forward facing respectivel
     y
     * {@link Orientation#W W} and {@link Orientation#E E}.
     */
    A
}
