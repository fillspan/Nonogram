package main;

public class ClusterMoves {
    private int[] moveArray;
    private int moves;
    private int leastLength;

    public ClusterMoves(int[] clusters, int length) throws Exception {
        moveArray = new int[clusters.length];
        int noOfMoves = 0;
        for (int i : clusters)
            noOfMoves += i;
        noOfMoves += (clusters.length - 1);
        leastLength = noOfMoves;
        noOfMoves = length - noOfMoves;
        if (noOfMoves < 0) {
            StringBuilder sb = new StringBuilder();
            for (int i : clusters)
                sb.append('[').append(i).append(']');
                throw new Exception("Length doesn't fit the clusters: " + sb.toString() + ", " + length);
        } else
            this.moves = noOfMoves;
    }

    public int getMove(int i) {
        if (moveArray[moveArray.length - 1] > moves)
            return -1;
        else
            return moveArray[i];
    }

    public int getLeastLength() {
        return leastLength;
    }

    public boolean next() {
        if (moveArray != null) {
            int index = moveArray.length - 1, step = 1;
            while (index - step >= 0) {
                if (moveArray[index] == moveArray[index - step])
                    step++;
                else {
                    index = index - step;
                    step = 1;
                }
            }
            moveArray[index]++;
            for (int i = index - 1; i >= 0; i--)
                moveArray[i] = 0;

            if (moveArray[moveArray.length - 1] > moves)
                moveArray = null;
        }

        return moveArray != null;
    }

    public static void main(String[] args) throws Exception {
        ClusterMoves cm = new ClusterMoves(new int[]{1, 1, 1, 1, 1}, 5);
        for (int i = 0; i < 30; i++) {
            System.out.println(cm);
            cm.next();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i : moveArray)
            sb.append("[").append(i).append("]");
        return sb.toString();
    }
}
