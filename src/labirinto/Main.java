package labirinto;

import java.util.ArrayList;
import java.util.List;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;

public class Main extends EngineFrame {

    private int largura_grid, altura_grid;
    private int origem_x, origem_y;
    private int[][] grid;
    private char[][] setas;
    private List<int[][]> lista;
    private List<char[][]> lista_setas;
    private int posicao, velocidade;
    double acumulador;

    public Main() {
        super(800, 600, "Labirinto", 30, true);
    }

    @Override
    public void create() {
        // String que representa os valores a serem inseridos no grid
        //  0 = espaço vazio
        //  1 = parede
        //  2 = destino
        //  3 = origem
        //  4 = espaço visitado anteriormente
        String texto =
        "3001000000000000\n" +
        "0011001000111111\n" +
        "0001001000000000\n" +
        "0111001000001110\n" +
        "0010121000101000\n" +
        "0010101010101011\n" +
        "0010101010101000\n" +
        "0110110010101110\n" +
        "0000010010101000\n" +
        "0000010010101011\n" +
        "0011110010101000\n" +
        "0000000010000000";

        // Looping pra descobrir a largura e a aultura do grid...
        largura_grid = 0;
        altura_grid = 1;
        for (char c : texto.toCharArray()) {
            if (c == '\n') {
                altura_grid++;
                largura_grid = 0;
            } else {
                largura_grid++;
            }
        }

        // Inicia o array que representa o grid...
        grid = new int[largura_grid][altura_grid];
        setas = new char[largura_grid][altura_grid];

        // Insere os valores da String no grid...
        int i = 0;
        int j = 0;
        for (char c : texto.toCharArray()) {
            if (c == '\n') {
                j++;
                i = 0;
            } else {
                grid[i][j] = (c - 48);
                setas[i][j] = ' ';

                if (c - 48 == 3) {
                    origem_x = i;
                    origem_y = j;
                }

                i++;
            }
        }

        // Posição inical da lista e velocidade inicial da simulação...
        posicao = 0;
        velocidade = 10;

        // Inicia a lista
        lista = new ArrayList<>();
        lista_setas = new ArrayList<>();
        copiar();

        // Resolve o labirinto...
        if (!labirinto(origem_x, origem_y)) {
            System.out.println("Não foi possível achar uma solução!");
        } else {
            System.out.println("Solução encontrada!");
        }
    }

    @Override
    public void update(double delta) {
        if (isKeyPressed(KEY_R)) {
            posicao = 0;
        }

        if (isKeyPressed(KEY_UP) && velocidade < 20) {
            velocidade++;
        }

        if (isKeyPressed(KEY_DOWN) && velocidade > 1) {
            velocidade--;
        }
        
        acumulador += delta;

        if (acumulador - (1 /((double)velocidade)) >= 0 && posicao < lista.size() - 1) {
            posicao++;
            acumulador = 0;
        }
    }

    @Override
    public void draw() {
        for (int i = 0; i < largura_grid; i++) {
            for (int j = 0; j < altura_grid; j++) {
                switch (lista.get(posicao)[i][j]) {
                    case 1:
                        fillRectangle(0 + (i * 45), 0 + (j * 45), 45, 45, BLACK);
                        break;

                    case 2:
                        fillRectangle(0 + (i * 45), 0 + (j * 45), 45, 45, (posicao == lista.size() - 1 ? GREEN : RED));
                        drawText("D", (14 + (i * 45)), (14 + (j * 45)), 32, BLACK);
                        break;

                    case 3:
                        fillRectangle(0 + (i * 45), 0 + (j * 45), 45, 45, GRAY);
                        break;

                    case 4:
                        fillRectangle(0 + (i * 45), 0 + (j * 45), 45, 45, BLUE);
                        drawText("" + lista_setas.get(posicao)[i][j], 12.5 + (i * 45), 12.5 + (j * 45), 24, BLACK);
                        break;
                }

                drawRectangle(0 + (i * 45), 0 + (j * 45), 45, 45, BLACK);
            }
        }
    
        drawText("i", (14 + (origem_x * 45)), (14 + (origem_y * 45)), 32, BLACK);
        drawText("(R) Reiniciar", 0, altura_grid * 45 + 2, 16, BLACK);
        drawText("Passos: " + posicao, 0, (altura_grid * 45 + 2) + 16, 16, BLACK);
        drawText("Velocidade: " + velocidade + " passos por segundo", 0, (altura_grid * 45 + 2)+ 32, 16, BLACK);
    }

    public boolean labirinto(int x, int y) {
        try {
            if (grid[x][y] != 1 && grid[x][y] != 4) {
                if (grid[x][y] == 2) {
                    return true;
                }

                grid[x][y] = 4;

                // DIREITA
                copiar();
                setas[x][y] = '→';
                if (labirinto(x + 1, y)) {
                    return true;
                }

                // ESQUERDA
                copiar();
                setas[x][y] = '←';
                if (labirinto(x - 1, y)) {
                    return true;
                }

                // BAIXO
                copiar();
                setas[x][y] = '↓';
                if (labirinto(x, y + 1)) {
                    return true;
                }

                // CIMA
                setas[x][y] = '↑';
                copiar();
                if (labirinto(x, y - 1)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        setas[x][y] = ' ';
        return false;
    }

    public void copiar() {
        int inter[][] = new int[largura_grid][altura_grid];
        char setas_inter[][] = new char[largura_grid][altura_grid];

        for (int i = 0; i < largura_grid; i++) {
            for (int j = 0; j < altura_grid; j++) {
                inter[i][j] = grid[i][j];
                setas_inter[i][j] = setas[i][j];
            }
        }

        lista_setas.add(setas_inter);
        lista.add(inter);
    }

    public static void main(String[] args) {
        new Main();
    }
}
