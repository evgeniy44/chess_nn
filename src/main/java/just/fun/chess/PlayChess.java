package just.fun.chess;

import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.Normalizer;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlayChess {

    public static void main(String[] args) throws IllegalMoveException, IOException {
        Pair<MultiLayerNetwork, Normalizer> pair = ModelSerializer.restoreMultiLayerNetworkAndNormalizer(
                new File("/Users/yberloh/IdeaProjects/chessnn/MyMultiLayerNetwork.zip"), true);
        MultiLayerNetwork network = pair.getLeft();

        Game game = new Game();
        Position position = game.getPosition();
        showPosition(position);
        System.out.println(position);
//        new GameBrowser(game).;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Enter command : ");
            float[] floats = ChessFetcher.translatePosition(position);
            INDArray output = network.output(Nd4j.create(floats));
            String input = br.readLine();
        }

//        for (short i : game.getPosition().getAllMoves()) {
//            game.getPosition().doMove(i);
//        };


//        game.getPosition().doMove(new Short("1"));
    }

    private static void showPosition(Position position) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int index = i*8 + j;

                int piece = position.getPiece(index);
                int color = position.getColor(index);
                if (piece == 5 && color == 0) {
                    builder.append("p");
                } else if (piece == 5 && color == 1) {
                    builder.append("P");
                } else if (piece == 3 && color == 0) {
                    builder.append("t");
                } else if (piece == 3 && color == 1) {
                    builder.append("T");
                } else if (piece == 1 && color == 0) {
                    builder.append("h");
                } else if (piece == 1 && color == 1) {
                    builder.append("H");
                } else if (piece == 2 && color == 0) {
                    builder.append("o");
                } else if (piece == 2 && color == 1) {
                    builder.append("O");
                } else {
                    builder.append(piece);
                }

            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }
}
