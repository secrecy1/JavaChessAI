public class TTEntry {
    int score;
    int depth;
    // move stuff
    int startCol;
    int startRow;
    int endCol;
    int endRow;
    public TTEntry(int score, int startCol, int startRow, int endCol, int endRow)
    {
        this.score = score;
        this.startCol = startCol;
        this.startRow = startRow;
        this.endCol = endCol;
        this.endRow = endRow;
    }
}
