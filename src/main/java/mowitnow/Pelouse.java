package mowitnow;

/**
 * From spec "pelouse", "surface", "surfaces rectangulaires" to an unique abstraction here.
 * From specs "fichier d'entrée comme [...] coordonnées du coin supérieur droit de la pelouse, celles du coin inférieur gauche sont supposées être
 * (0,0)". The grid (or "grille") of this Pelouse can then be defined quite quickly with the the static point
 * ({@link Pelouse#MIN_X MIN_X}, {@link Pelouse#MIN_Y MIN_Y}) and the dynamic one
 * ({@link Pelouse#getMaxX() getMaxX()}, {@link Pelouse#getMaxY() getMaxY()}).
 */
public interface Pelouse {
    Integer MIN_X = 0;
    Integer MIN_Y = 0;
    Integer getMaxX();
    Integer getMaxY();
}
