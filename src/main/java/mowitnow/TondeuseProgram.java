package mowitnow;

import mowitnow.infra.TondeuseProgramInputs;

import java.io.File;
import java.util.List;

public interface TondeuseProgram {

    Tondeuse control(final Tondeuse tondeuse, final List<TondeuseCommand> commands, final Pelouse pelouse);

    /**
     * Ultimate function here, not partial like others.
     * from specs :
     * "Pour programmer la tondeuse, on lui fournit un fichier d'entrée construit comme suit :
     *
     * La première ligne correspond aux coordonnées du coin supérieur droit de la pelouse, celles du coin inférieur gauche sont supposées être (0,0)
     * La suite du fichier permet de piloter toutes les tondeuses qui ont été déployées. Chaque tondeuse a deux lignes la concernant :
     * la première ligne donne la position initiale de la tondeuse, ainsi que son orientation. La position et l'orientation sont fournies sous la forme de 2 chiffres et d’une lettre, séparés par un espace
     * la seconde ligne est une série d'instructions ordonnant à la tondeuse d'explorer la pelouse. Les instructions sont une suite de caractères sans espaces."
     * It means translation can be done:
     * - from first line to {@link Pelouse}
     * - from next pairs of lines, pairs of {@link Tondeuse} and {@link List} of {@link TondeuseCommand}s
     * Under the hood, one need a single steady middle encapsulating data structure to process those here,
     * like {@link TondeuseProgramInputs}.
     */
    List<Tondeuse> control(final File fichierDentreePath);
}
