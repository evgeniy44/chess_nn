package just.fun.chess;

import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;
import just.fun.chess.board.FloatArrayHolder;
import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.board.SimpleMove;
import org.apache.commons.lang3.ArrayUtils;
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

    private enum ChessCells {
        A("a", 0),
        B("b", 1),
        C("c", 2),
        D("d", 3),
        E("e", 4),
        F("f", 5),
        G("g", 6),
        H("h", 7);

        private final String letter;
        private final int val;

        ChessCells(String letter, int val) {
            this.letter = letter;
            this.val = val;
        }

        public static int from(String letter) {
            for (ChessCells chessCells : values()) {
                if (chessCells.letter.equals(letter)) {
                    return chessCells.val;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) throws IllegalMoveException, IOException {
        Pair<MultiLayerNetwork, Normalizer> pair = ModelSerializer.restoreMultiLayerNetworkAndNormalizer(
                new File("/Users/yberloh/IdeaProjects/chessnn/MyMultiLayerNetwork.zip"), true);
        MultiLayerNetwork network = pair.getLeft();

        Game game = new Game();
        Position position = game.getPosition();
        showPosition(position);
        System.out.println(position);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Enter command : ");
            String input = br.readLine();
            short myMove = 0;
            try {
                myMove = toMove(game, input);
            } catch (Exception e) {
                System.out.println("Invalid move: " + input);
                continue;
            }
            printMove("My move: ", myMove);
            game.getPosition().doMove(myMove);

            showPosition(position);

            short move  = getSuitableMove(game, network);
            printMove("Jora's move: ", move);
            game.getPosition().doMove(move);
            showPosition(position);
        }
    }

    private static void printMove(String prefix, short move) {
        System.out.println(prefix + Move.getString(move));
    }

    private static short toMove(Game game, String input) {
        String[] fromTo = input.split(" ");
        String letter = fromTo[0].substring(0, 1);
        int num = Integer.parseInt(fromTo[0].substring(1, 2));
        int letterNum = ChessCells.from(letter);
        int from = ((num - 1) * 8)  + letterNum;

        letter = fromTo[1].substring(0, 1);
        num = Integer.parseInt(fromTo[1].substring(1, 2));
        letterNum = ChessCells.from(letter);
        int to = ((num - 1) * 8)  + letterNum;
        for (short i : game.getPosition().getAllMoves()) {
            if (Move.getFromSqi(i) == from && Move.getToSqi(i) == to) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    private static short getSuitableMove(Game game, MultiLayerNetwork network) {
        byte[] positionVec = new PositionConverter().convert(game.getPosition()).getArray();
        short bestMove = 0;
        float bestScore = 0;
        for (short move : game.getPosition().getAllMoves()) {
            byte[] possibleMoveVec
                    = new MoveConverter().convert(new SimpleMove(Move.getFromSqi(move), Move.getToSqi(move), game.getPosition().getToPlay())).getArray();
            byte[] input = ArrayUtils.addAll(positionVec, possibleMoveVec);
            INDArray output = network.output(Nd4j.create(toFloatArray(input)));
            float score = output.getFloat(0);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private static float[] toFloatArray(byte[] byteArray) {
        float[] doubles = new float[byteArray.length];

        for(int i = 0; i < doubles.length; ++i) {
            doubles[i] = byteArray[i];
        }

        return doubles;
    }

    public static void showPosition(Position position) {
        StringBuilder builder = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                int index = i * 8 + j;

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
                } else if (piece == 0) {
                    builder.append(" ");
                } else {
                    builder.append(piece);
                }

            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }
}
