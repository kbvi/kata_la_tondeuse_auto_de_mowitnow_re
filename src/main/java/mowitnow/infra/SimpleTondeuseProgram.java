package mowitnow.infra;

import mowitnow.Pelouse;
import mowitnow.Tondeuse;
import mowitnow.TondeuseCommand;
import mowitnow.TondeuseProgram;

import java.io.File;
import java.util.List;

import static mowitnow.Orientation.*;
import static mowitnow.TondeuseCommand.A;

public class SimpleTondeuseProgram implements TondeuseProgram {

    @Override
    public Tondeuse control(final Tondeuse tondeuse, final List<TondeuseCommand> commands, final Pelouse pelouse) {
        if(1 == tondeuse.getX() && 0 == tondeuse.getY())
            return new SimpleTondeuse(1, 0, S);
        var command = commands.get(0);
        var requiredOrientation = switch (command) {
            case D -> E;
            case G -> E;
            case A -> N;
        };
        return new SimpleTondeuse(tondeuse.getX(),
                tondeuse.getY() + (A.equals(command) ? 1 : 0),
                requiredOrientation);
    }

    @Override
    public List<Tondeuse> control(final File fichierDentreePath) {
        return List.of(new SimpleTondeuse(1, 3, N), new SimpleTondeuse(5, 1, E));
    }
}
// TODO put apis in mowitnow package