import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.ArrayList;

public class TCPServer {

    private static int[][] binaryTreeMultiply(List<int[][]> matrices) {

        // Executor for parallel tasks
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (matrices.size() > 1) {
            List<Future<int[][]>> futureResults = new ArrayList<>();

            // Pair matrices and submit tasks for multiplication
            for (int i = 0; i < matrices.size(); i += 2) {
                int[][] matrix1 = matrices.get(i);
                int[][] matrix2 = matrices.get(i + 1);

                Callable<int[][]> task = () -> strassenMultiply(matrix1, matrix2);
                futureResults.add(executor.submit(task));
            }

            // Collect results of this level
            List<int[][]> nextLevelMatrices = new ArrayList<>();
            for (Future<int[][]> future : futureResults) {
                try {
                    nextLevelMatrices.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    executor.shutdown();
                    return null;
                }
            }

            // Prepare for the next level
            matrices = nextLevelMatrices;
        }

        // Shutdown executor
        executor.shutdown();

        // Return the final result
        return matrices.getFirst();
    }

    private static int[][] strassenMultiply(int[][] A, int[][] B) {
        int n = A.length;
        if (n == 1) {
            return new int[][]{{A[0][0] * B[0][0]}};
        }

        int newSize = n / 2;
        int[][] a11 = new int[newSize][newSize];
        int[][] a12 = new int[newSize][newSize];
        int[][] a21 = new int[newSize][newSize];
        int[][] a22 = new int[newSize][newSize];
        int[][] b11 = new int[newSize][newSize];
        int[][] b12 = new int[newSize][newSize];
        int[][] b21 = new int[newSize][newSize];
        int[][] b22 = new int[newSize][newSize];

        // Splitting matrices into sub-matrices
        split(A, a11, 0, 0);
        split(A, a12, 0, newSize);
        split(A, a21, newSize, 0);
        split(A, a22, newSize, newSize);
        split(B, b11, 0, 0);
        split(B, b12, 0, newSize);
        split(B, b21, newSize, 0);
        split(B, b22, newSize, newSize);

        // Strassen's Algorithm calculations
        int[][] M1 = strassenMultiply(add(a11, a22), add(b11, b22));
        int[][] M2 = strassenMultiply(add(a21, a22), b11);
        int[][] M3 = strassenMultiply(a11, subtract(b12, b22));
        int[][] M4 = strassenMultiply(a22, subtract(b21, b11));
        int[][] M5 = strassenMultiply(add(a11, a12), b22);
        int[][] M6 = strassenMultiply(subtract(a21, a11), add(b11, b12));
        int[][] M7 = strassenMultiply(subtract(a12, a22), add(b21, b22));

        int[][] c11 = add(subtract(add(M1, M4), M5), M7);
        int[][] c12 = add(M3, M5);
        int[][] c21 = add(M2, M4);
        int[][] c22 = add(subtract(add(M1, M3), M2), M6);

        // Combining sub-matrices into the result matrix
        int[][] C = new int[n][n];
        join(c11, C, 0, 0);
        join(c12, C, 0, newSize);
        join(c21, C, newSize, 0);
        join(c22, C, newSize, newSize);

        return C;
    }

    // Helper methods for matrix operations

    private static int[][] add(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    private static int[][] subtract(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }

    private static void split(int[][] P, int[][] C, int iB, int jB) {
        for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++) {
            for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++) {
                C[i1][j1] = P[i2][j2];
            }
        }
    }

    private static void join(int[][] C, int[][] P, int iB, int jB) {
        for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++) {
            for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++) {
                P[i2][j2] = C[i1][j1];
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        int portNumber = 55556;
        String hostName = "localhost";
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {

            socket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.err.println("Couldn't connect to router: " + hostName);
            System.exit(1);
        }

        System.out.println("Connected to ServerRouter.");

        List<int[][]> fromRouter;

        fromRouter = (List<int[][]>) in.readObject();

        System.out.println("Server: Matrices received from router. Multiplying...");

        int numberOfMatrices = fromRouter.size();
        int matrixSize = fromRouter.getFirst().length;

        /// STRASSEN SHIT BELOW ///


        int[][] fromServer = binaryTreeMultiply(fromRouter);


        /// STRASSEN SHIT ABOVE ///

        System.out.println("Server: Matrices Multiplied. Sending response... ");
        out.writeObject(fromServer);



        out.close();
        in.close();
        socket.close();
    }
}
