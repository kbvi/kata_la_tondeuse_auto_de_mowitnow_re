package mowitnow.infra;

import mowitnow.Pelouse;
import mowitnow.Tondeuse;
import mowitnow.TondeuseCommand;
import mowitnow.TondeuseProgram;

import java.util.List;

/**
 * What's necessary and sufficient ultimately for a {@link TondeuseProgram tondeuse program} to compute.
 * From specs "passer le test ci-après [...] Les données en entrée sont injectées sous forme de fichier." So the inputs
 * are
 */
public record TondeuseProgramInputs(Pelouse pelouse,
                                    Tondeuse tondeuse,
                                    List<TondeuseCommand> commands) {
}
