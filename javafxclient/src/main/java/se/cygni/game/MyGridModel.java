package se.cygni.game;

import eu.lestard.grid.Cell;
import eu.lestard.grid.GridModel;

public class MyGridModel<State> extends GridModel<State> {

    /**
     * Return the cell with the given coordinates.
     * @param column
     * @param row
     * @return
     */
    public Cell<State> getCell(final int column, final int row) {

        // We are initializing
        if (getNumberOfColumns()*getNumberOfRows() > cells().size()) {
            return null;
        }

        int pos = row + column * getNumberOfRows();

        if (pos < cells().size())
            return cells().get(pos);

        return null;
//        This was the earlier implementation. Crazy when there are 100x100 tiles
//        and this method is called multiple times (all the time)
//        return cells().stream()
//            .filter(cell ->
//                (cell.getColumn() == column && cell.getRow() == row))
//            .findFirst()
//            .orElse(null);
    }

}
