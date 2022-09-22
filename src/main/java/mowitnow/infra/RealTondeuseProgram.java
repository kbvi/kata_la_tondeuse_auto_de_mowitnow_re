package mowitnow.infra;

import mowitnow.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static mowitnow.Orientation.*;
import static mowitnow.infra.OptionalFromThrowableHelper.ofThrowableAndNullable;

public class RealTondeuseProgram implements TondeuseProgram {

    public Tondeuse control(final Tondeuse tondeuse,
                            final List<TondeuseCommand> commands,
                            final Pelouse pelouse) {
        return commands.stream().reduce(
                tondeuse,
                (movingTondeuse, command) ->
                        switch (command) {
                            case D, G -> getTondeuse(
                                    movingTondeuse.getX(),
                                    movingTondeuse.getY(),
                                    turn(movingTondeuse.getOrientation(), command)
                            );
                            case A -> getTondeuse(
                                    getNextHorizontalPosition(pelouse, movingTondeuse),
                                    getNextVerticalPosition  (pelouse, movingTondeuse),
                                    movingTondeuse.getOrientation()
                            );
                        },
                (a, b) -> a
        );
    }

    public Tondeuse getTondeuse(final Integer x, final Integer y, final Orientation orientation) {
        return new SimpleTondeuse(x, y, orientation);
    }

    public Pelouse  getPelouse(final Integer x, final Integer y) {
        return new SimplePelouse(x, y);
    }


    private int getNextVerticalPosition(final Pelouse pelouse, final Tondeuse movingTondeuse) {
        return movingTondeuse.getY() + (
                isTondeuseOrientedVertically(movingTondeuse)
                        && !isAboutToGetOutOfLowerBound(movingTondeuse, Pelouse.MIN_Y    , movingTondeuse.getY(), S)
                        && !isAboutToGetOutOfUpperBound(movingTondeuse, pelouse.getMaxY(), movingTondeuse.getY(), N)
                        ? 1 - movingTondeuse.getOrientation().ordinal()
                        : 0
        );
    }

    private boolean isAboutToGetOutOfUpperBound(final Tondeuse movingTondeuse,
                                                final Integer pelouseMax,
                                                final Integer movingTondeusePoint,
                                                final Orientation forward) {
        return pelouseMax <= movingTondeusePoint && forward.equals(movingTondeuse.getOrientation());
    }

    private boolean isAboutToGetOutOfLowerBound(final Tondeuse movingTondeuse,
                                                final Integer pelouseMin,
                                                final Integer tondeuseMovingPoint,
                                                final Orientation backward) {
        return pelouseMin >= tondeuseMovingPoint && backward.equals(movingTondeuse.getOrientation());
    }

    private boolean isTondeuseOrientedVertically(final Tondeuse movingTondeuse) {
        return movingTondeuse.getOrientation().ordinal() % 2 == 0;
    }

    private boolean isTondeuseOrientedHorizontically(final Tondeuse movingTondeuse) {
        return !isTondeuseOrientedVertically(movingTondeuse);
    }

    private int getNextHorizontalPosition(final Pelouse pelouse, final Tondeuse movingTondeuse) {
        return movingTondeuse.getX() + (
                isTondeuseOrientedHorizontically(movingTondeuse)
                        && !isAboutToGetOutOfLowerBound(movingTondeuse, Pelouse.MIN_X    , movingTondeuse.getX(), W)
                        && !isAboutToGetOutOfUpperBound(movingTondeuse, pelouse.getMaxX(), movingTondeuse.getX(), E)
                        ? getOneHorizontalMove(movingTondeuse)
                        : 0
        );
    }

    private int getOneHorizontalMove(final Tondeuse movingTondeuse) {
        return 2 - movingTondeuse.getOrientation().ordinal();
    }

    // I used term rewriting here because I knew all cases were covered
    private Orientation turn(final Orientation fromOrientation, final TondeuseCommand turningCommand) {
        final int orientationOrdinal = fromOrientation.ordinal();
        final int commandOrdinal = turningCommand.ordinal();
        final int maxIdxOrientation = Orientation.values().length - 1;
        return Orientation.values()[
                (-2 * commandOrdinal + 1) * orientationOrdinal + (commandOrdinal - 1) * maxIdxOrientation < 0
                        ? orientationOrdinal - 2 * commandOrdinal + 1
                        : commandOrdinal * maxIdxOrientation
                ];
    }

// With a fichierdentree part :

    @Override
    public List<Tondeuse> control(final File fichierDentreePath) {
        try (final Stream<String> content = Files.lines(Paths.get(fichierDentreePath.getAbsolutePath()))) {
            final Entry<Integer, List<TondeuseProgramInputs>> indexedEmptyInputs =
                    new SimpleEntry<>(1, Collections.emptyList());
            final Entry<Integer, List<TondeuseProgramInputs>> integralInputs =
                    content.reduce(
                            indexedEmptyInputs,
                            (programInputs, line) -> lineToProgramInputs(line, programInputs),
                            (a, b) -> a
                    );
            return integralInputs
                    .getValue()
                    .stream()
                    .map(tondeuseProgramInputs -> control(
                            tondeuseProgramInputs.tondeuse(),
                            tondeuseProgramInputs.commands(),
                            tondeuseProgramInputs.pelouse()))
                    .toList();
        } catch (IOException e) {
            System.err.println("Problem reading file " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private Entry<Integer, List<TondeuseProgramInputs>> lineToProgramInputs(
            final String line,
            final Entry<Integer, List<TondeuseProgramInputs>> indexedProgramInputs
    ) {
        if(indexedProgramInputs.getKey() == 1) {
            return extractPelouseFromLineToProgramInputs (line, indexedProgramInputs);
        } else if (indexedProgramInputs.getKey() % 2 == 0) {
            return extractTondeuseFromLineToProgramInputs(line, indexedProgramInputs);
        }// else if (indexedProgramInputs.getKey() % 2 != 0) {
        return extractCommandsFromLineToProgramInputs(line, indexedProgramInputs);
        //}
    }

    private Entry<Integer, List<TondeuseProgramInputs>>
    extractCommandsFromLineToProgramInputs(final String line,
                                           final Entry<Integer, List<TondeuseProgramInputs>> indexedProgramInputs) {
        return ofThrowableAndNullable(() -> Arrays.stream(line.split("")).map(TondeuseCommand::valueOf))
                .map(commands ->
                        nextlineToInputs(indexedProgramInputs.getKey(),
                                Stream.concat(
                                                indexedProgramInputs.getValue()
                                                        .stream()
                                                        .limit(indexedProgramInputs.getValue().size() - 1),
                                                indexedProgramInputs.getValue()
                                                        .stream()
                                                        .skip (indexedProgramInputs.getValue().size() - 1)
                                                        .map(tondeuseProgramInputs ->
                                                                new TondeuseProgramInputs(
                                                                        tondeuseProgramInputs.pelouse(),
                                                                        tondeuseProgramInputs.tondeuse(),
                                                                        commands.toList())))
                                        .toList()))
                .orElseThrow(() -> new IllegalArgumentException("each odd line of injecting mowitnow tondeuse " +
                        "program file must have a string composed only of D, G or A in order to be valid " +
                        "consecutive commands to mower"));
    }

    private Optional<Pelouse> splitLineIntoPelouse(final String line) {
        final String[] parts = line.split("\\s+");
        if(parts.length == 2) {
            final Optional<Integer> maybeMaxX = intExtracting(parts,0);
            final Optional<Integer> maybeMaxY = intExtracting(parts,1);
            if (maybeMaxX.isPresent() && maybeMaxY.isPresent()) {
                return Optional.of(getPelouse(maybeMaxX.get(), maybeMaxY.get()));
            }
        }
        return Optional.empty();
    }

    private Entry<Integer, List<TondeuseProgramInputs>>
    extractPelouseFromLineToProgramInputs(final String line,
                                          final Entry<Integer, List<TondeuseProgramInputs>> indexedProgramInputs) {
        return splitLineIntoPelouse(line)
                .map(pelouse -> nextlineToInputs(indexedProgramInputs.getKey(), List.of(new TondeuseProgramInputs(pelouse, null, Collections.emptyList()))))
                .orElseThrow(() -> new IllegalArgumentException("first line of injecting mowitnow tondeuse program" +
                        " file must have two integer part in order to be a valid surface or lawn to mow"));
    }

    private Entry<Integer, List<TondeuseProgramInputs>>
    extractTondeuseFromLineToProgramInputs(final String line,
                                           final Entry<Integer, List<TondeuseProgramInputs>> indexedProgramInputs) {
        return splitLineIntoTondeuse(line).map(tondeuse ->
                nextlineToInputs(
                        indexedProgramInputs.getKey(),
                        Stream.concat(
                                indexedProgramInputs.getValue()
                                        .stream()
                                        .filter(tondeuseProgramInputs -> Objects.nonNull(tondeuseProgramInputs.tondeuse())),
                                indexedProgramInputs.getValue()
                                        .stream()
                                        .findFirst()
                                        .map(TondeuseProgramInputs::pelouse)
                                        .map(pelouse -> new TondeuseProgramInputs(pelouse, tondeuse, Collections.emptyList()))
                                        .stream()
                        ).toList())).orElseThrow(() -> new IllegalArgumentException("each even line of injecting mowitnow" +
                " tondeuse program file must have two integer parts then a single N, E, W or S in order to be a" +
                " valid mower to mow automatically"));
    }

    private Optional<Tondeuse> splitLineIntoTondeuse(final String line) {
        final String[] parts = line.split("\\s+");
        if(parts.length == 3) {
            final Optional<Integer> maybeX = intExtracting(parts,0);
            final Optional<Integer> maybeY = intExtracting(parts,1);
            final Optional<Orientation> maybeOrientation = ofThrowableAndNullable(() -> Orientation.valueOf(parts[2]));
            if (maybeX.isPresent() && maybeY.isPresent() && maybeOrientation.isPresent()) {
                return Optional.of(getTondeuse(maybeX.get(), maybeY.get(), maybeOrientation.get()));
            }
        }
        return Optional.empty();
    }

    private Optional<Integer> intExtracting(final String[] parts, final Integer idx) {
        return ofThrowableAndNullable(() -> Integer.valueOf(parts[idx]));
    }

    private Entry<Integer, List<TondeuseProgramInputs>> nextlineToInputs(final int lineNumber,
                                                                         final List<TondeuseProgramInputs> programInputs) {
        return new SimpleEntry<>(lineNumber + 1, programInputs);
    }
}
