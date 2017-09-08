package eisti.eu.MRO.morpion;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Projet : Morpion
 * <p/>
 * Created by Michaël on 24/09/2015.
 * <p/>
 * Classe qui contient le moteur du jeu
 */
public class GameEngine {
    /**
     * Tag pour la console
     */
    private static final String TAG = "GameEngine";
    /**
     * Liste des coups gagnants pour vérification déterministe rapide
     */
    private static ArrayList<Grid> winning_grid_list_ = new ArrayList<>();
    /**
     * Joueur courant
     */
    private PlayerType current_player_;
    /**
     * Joueur qui a commencé la partie
     */
    private PlayerType starting_player_;
    /**
     * Gagnant
     */
    private PlayerType winner_;
    /**
     * Combinaison gagnate
     */
    private Grid winning_combination_;
    /**
     * Score du joueur
     */
    private int score_player_;
    /**
     * Score de l'ordinateur
     */
    private int score_opponent_;
    /**
     * Nombre de parties jouées
     */
    private int game_counter_;
    /**
     * Contexte
     */
    private MorpionActivity context_;
    /**
     * Handler de toast pour dire au joueur de jouer
     */
    private Handler delayed_handler_;
    /**
     * Toast courant, pour être tué par le webApp quand le joueur a joué
     */
    private Runnable player_toaster_;

    /**
     * Construit un nouveau moteur de jeu de morpion
     *
     * @param ctx             contexte
     * @param starting_player joueur qui commence la partie
     */
    public GameEngine(MorpionActivity ctx, PlayerType starting_player) {
        starting_player_ = starting_player;
        current_player_ = starting_player_;
        winner_ = PlayerType.None;
        context_ = ctx;

        score_player_ = 0;
        score_opponent_ = 0;
        game_counter_ = 0;

        delayed_handler_ = new Handler();
    }

    /**
     * Fonction interne qui va vérifier si une grille a une combinaison gagnante
     *
     * @param whoToCheck  Joueur candidat pour gagner
     * @param whatToCheck Elément que joue ce joueur
     * @param grid        Grille candidate
     * @return une paire avec le Joueur qui a gagné et sa combinaison gagnante
     */
    private static Pair<PlayerType, Grid> hasWinnerInternal(PlayerType whoToCheck, Grid.CelElement whatToCheck, Grid grid) {
        Log.i(TAG, "what to check : " + whatToCheck.toString());
        Log.i(TAG, "who to check : " + whoToCheck.toString());
        for (Grid g : winning_grid_list_) {
            if (compare(g, grid, whatToCheck)) {
                Pair<PlayerType, Grid> ret = new Pair<>(whoToCheck, g);
                Log.i(TAG, "Joueur " + ret.first.toString() + " a gagné avec cette combinaison : ");
                ret.second.dumpToConsole();
                return ret;
            }
        }
        return new Pair<>(PlayerType.Computer.None, new Grid(grid.getSize()));
    }

    /**
     * Compare une grille "whatever" et une grille candidate (et un type d'élément)
     * Si la grille whatever est la combinaison gagnante contenue dans la grille candidate alors on retourne vrai
     *
     * @param whateverGrid grille générique de combinaison gagnante
     * @param testedGrid   grille candidate
     * @param whatToCheck  élément à tester dans la grille candidate
     * @return vrai si la grille générique est la combinaison gagnante dans grille candidate
     */
    private static boolean compare(Grid whateverGrid, Grid testedGrid, Grid.CelElement whatToCheck) {
        for (int i = 0; i < whateverGrid.getSize(); i++) {
            for (int j = 0; j < whateverGrid.getSize(); j++) {
                if (whateverGrid.isWhatever(i, j) && !testedGrid.isEqual(i, j, whatToCheck)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Retourne l'élément PlayerType opposé à celui passé en paramètre
     *
     * @param player joueur à opposer
     * @return l'opposé du joueur
     */
    public static PlayerType getOppositePlayer(PlayerType player) {
        return player == PlayerType.Computer ? PlayerType.Human : PlayerType.Computer;
    }

    /**
     * Initialisation type lazy de la liste des grilles de combinaisons gagnante
     * Static & lazy pour que plusieurs engine en même temps puissent tourner sans avoir à refaire le taff à chaque fois
     */
    private static void lazyInit() {
        /*
        Sur une grille type
        [[0,0], [0,1], [0,2]]
        [[1,0], [1,1], [1,2]]
        [[2,0], [2,1], [2,2]]
        Les combinaisons gagnantes sont :
        [[0,0], [0,1], [0,2]] ou [[1,0], [1,1], [1,2]] ou [[2,0], [2,1], [2,2]] (en ligne) ou
        [[0,0], [1,0], [2,0]] ou [[0,1], [1,1], [2,1]] ou [[0,2], [1,2], [2,2]] (en colonne) ou
        [[0,0], [1,1], [2,2]] ou [[0,2], [1,1], [2,0] en diagonale
         */

        //Lazy initialisation du tableau
        if (winning_grid_list_.isEmpty()) {
            /* Lignes */
            Grid win_line_1 = new Grid(3);
            win_line_1.setValue(0, 0, Grid.CelElement.Whatever);
            win_line_1.setValue(0, 1, Grid.CelElement.Whatever);
            win_line_1.setValue(0, 2, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_line_1);

            Grid win_line_2 = new Grid(3);
            win_line_2.setValue(1, 0, Grid.CelElement.Whatever);
            win_line_2.setValue(1, 1, Grid.CelElement.Whatever);
            win_line_2.setValue(1, 2, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_line_2);

            Grid win_line_3 = new Grid(3);
            win_line_3.setValue(2, 0, Grid.CelElement.Whatever);
            win_line_3.setValue(2, 1, Grid.CelElement.Whatever);
            win_line_3.setValue(2, 2, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_line_3);

            /* Colonnes */
            Grid win_col_1 = new Grid(3);
            win_col_1.setValue(0, 0, Grid.CelElement.Whatever);
            win_col_1.setValue(1, 0, Grid.CelElement.Whatever);
            win_col_1.setValue(2, 0, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_col_1);

            Grid win_col_2 = new Grid(3);
            win_col_2.setValue(0, 1, Grid.CelElement.Whatever);
            win_col_2.setValue(1, 1, Grid.CelElement.Whatever);
            win_col_2.setValue(2, 1, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_col_2);

            Grid win_col_3 = new Grid(3);
            win_col_3.setValue(0, 2, Grid.CelElement.Whatever);
            win_col_3.setValue(1, 2, Grid.CelElement.Whatever);
            win_col_3.setValue(2, 2, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_col_3);

            /* Diagonales */
            Grid win_diag_1 = new Grid(3);
            win_diag_1.setValue(0, 0, Grid.CelElement.Whatever);
            win_diag_1.setValue(1, 1, Grid.CelElement.Whatever);
            win_diag_1.setValue(2, 2, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_diag_1);

            Grid win_diag_2 = new Grid(3);
            win_diag_2.setValue(0, 2, Grid.CelElement.Whatever);
            win_diag_2.setValue(1, 1, Grid.CelElement.Whatever);
            win_diag_2.setValue(2, 0, Grid.CelElement.Whatever);
            winning_grid_list_.add(win_diag_2);
        }
    }

    /**
     * Retourne le joueur gagnant de la partie
     *
     * @return joueur gagnat
     */
    public PlayerType getWinner() {
        return winner_;
    }

    /**
     * Retourne la combinaison gagnate
     *
     * @returncombinaison gagnante
     */
    public Grid getWinningCombination() {
        return winning_combination_;
    }

    /**
     * Retourne le score du joueur
     *
     * @return score du joueur
     */
    public int getScorePlayer() {
        return score_player_;
    }

    /**
     * Retourne le score de l'adversaire
     *
     * @return score de l'adversaire
     */
    public int getScoreOpponent() {
        return score_opponent_;
    }

    /**
     * Retourne le nombre de parties jouées
     *
     * @return nombre de parties jouées
     */
    public int getGameCounter() {
        return game_counter_;
    }

    /**
     * Remet les valeurs "gagnantes" à 0 et lance une nouvelle partie
     */
    public void newGame() {
        starting_player_ = getOppositePlayer(starting_player_);
        current_player_ = starting_player_;
        winner_ = PlayerType.None;
        winning_combination_ = null;

        Log.i(TAG, "Lancement d'une nouvelle première partie...");
        //On lance avec swapTurn (donc il faut inverser le current_player, sauf que c'est déjà fait avec newGame() !)
        swapTurn();
    }

    /**
     * Fonction principale
     * Si la partie est finie alors elle update les score puis notifie la main activity (controler) pour update des cohérences sur les vues
     * Sinon, elle joue soit le tour de l'ordinateur, soit du joueur
     */
    public void swapTurn() {
        //Il peut y avoir match nul
        if (isFinished()) {
            game_counter_++;
            if (getWinner() == PlayerType.None) {
                context_.showToast(context_.getString(R.string.noone_win));
            } else if (getWinner() == PlayerType.Computer) {
                context_.showToast(context_.getString(R.string.computer_win));
                score_opponent_++;
            } else if (getWinner() == PlayerType.Human) {
                context_.showToast(context_.getString(R.string.player_win));
                score_player_++;
            }
            context_.finishGame();
        } else {

            //On swap, et on joue
            current_player_ = getOppositePlayer(current_player_);

            if (current_player_ == PlayerType.Computer) {
                context_.showToast(context_.getString(R.string.computer_about_to_play));
                delayed_handler_.postDelayed(new Runnable() {
                    public void run() {
                        doComputerTurn();
                        swapTurn();
                    }
                }, 2000);
            } else {
                doPlayerTurn();
                //C'est le controler WebApp qui appellera le swapTurn quand le joueur aura joué (notifié depuis javascript)
            }
        }
    }

    /**
     * Cette fonction s'appelle en boucle jusqu'à ce que le controler WebApp soit notifié par javascript et kill
     * le task qui s'appelle en boucle pour afficher le toaster d'invitation à jouer
     */
    private void doPlayerTurn() {
        player_toaster_ = new Runnable() {
            public void run() {
                context_.showToast(context_.getString(R.string.player_turn_to_play));
                doPlayerTurn();
            }
        };
        delayed_handler_.postDelayed(player_toaster_, 8000);
    }

    /**
     * Kill le task qui toast le joueur pour l'inviter à jouer
     */
    public void cancelDelayedToaster() {
        delayed_handler_.removeCallbacks(player_toaster_);
    }

    /**
     * Fait jouer l'ordinateur
     */
    public void doComputerTurn() {
        final Pair<Integer, Integer> move = doComputerTurnInternal();
        Log.i(TAG, "IA trouve : (" + move.first + ", " + move.second + ")=" + Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT));

        //Update modèle
        if (!context_.getGrid().setValueSafe(move.first, move.second, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT))) {
            throw new RuntimeException("IA invalid result !");
        } else {
            //Update vue
            context_.getWebView().post(new Runnable() {
                @Override
                public void run() {
                    context_.getWebApp().showGridElem(move.first, move.second, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT));
                }
            });
        }
    }

    /**
     * Coeur de l'IA
     * Concernant les difficultés : choix arbitraire des features
     * L'IA facile va prendre le centre, va empecher le joueur de gagner puis va jouer au petit bonheur la chance
     * L'IA normale va prendre le centre, va gagner même si le joueur peut gagner, va empecher le joueur de gagner puis va jouer au petit bonheur la chance
     * L'IA impossible va prendre le centre, va gagner même si le joueur peut gagner, va empecher le joueur de gagner puis va jouer *en maximisant ses chances* !
     *
     * @return la combinaison que l'ordinateur veut jouer
     */
    private Pair<Integer, Integer> doComputerTurnInternal() {
        //On a besoin des combinaisons gagnantes
        lazyInit();

        Grid grid_cur = context_.getGrid();

        //Première étape : si le centre est libre, on le prend (théoriquement, player ne peut pas gagner à ce moment du jeu)
        if (grid_cur.isEmpty(1, 1)) {
            return new Pair<>(1, 1);
        }

        //Coups possibles
        ArrayList<Pair<Integer, Integer>> possible_match = new ArrayList<>();
        for (int x = 0; x < grid_cur.getSize(); x++) {
            for (int y = 0; y < grid_cur.getSize(); y++) {
                if (grid_cur.isEmpty(x, y)) {
                    possible_match.add(new Pair<>(x, y));
                }
            }
        }

        //Que pour les IA normale et impossibles
        if (context_.getDifficultyConsistency().getDifficulty() != DifficultyConsistency.DifficultyLevel.Easy) {
            //2ème étape
            //On check si on peut gagner
            //Pour chaque coup possible, on fait une nouvelle grille et on check
            for (Pair<Integer, Integer> move : possible_match) {
                Grid grid_cpy = grid_cur.clone();

                grid_cpy.setValue(move.first, move.second, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT));

                //On vérifie si on peut gagner
                Pair<PlayerType, Grid> winner = hasWinnerInternal(PlayerType.Computer, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT), grid_cpy);
                if (winner.first == PlayerType.Computer) {
                    //Si oui, alors on retourne la combinaison gagnante
                    return new Pair<>(move.first, move.second);
                }
            }
        }

        //3nd étape :
        //On check si le joueur peut gagner
        //On fait des simulation de la même manière que dans l'étape 2
        for (Pair<Integer, Integer> move : possible_match) {
            Grid grid_cpy = grid_cur.clone();

            grid_cpy.setValue(move.first, move.second, MorpionActivity.PLAYER_ELEMENT);

            //On vérifie si le joueur gagne
            Pair<PlayerType, Grid> winner = hasWinnerInternal(PlayerType.Human, MorpionActivity.PLAYER_ELEMENT, grid_cpy);
            if (winner.first == PlayerType.Human) {
                //Si oui, alors on retourne la combinaison à bloquer
                return new Pair<>(move.first, move.second);
            }
        }

        //4ème étape, on ne peut ni perdre, ni gagner
        //Alors on va lister les coups possibles (cases libres) et les cases déjà prises par le PC
        //On va ensuite voir, grâce à la liste des coups gagnants possibles, là où c'est le plus intéligent de jouer
        //C'est à dire : il faut jouer là où on peut gagner au coup suivant

        //Coups joués (par le pc)
        ArrayList<Pair<Integer, Integer>> already_played = new ArrayList<>();
        for (int x = 0; x < grid_cur.getSize(); x++) {
            for (int y = 0; y < grid_cur.getSize(); y++) {
                if (grid_cur.isEqual(x, y, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT))) {
                    already_played.add(new Pair<>(x, y));
                }
            }
        }

        //Petit bonheur la chance : IA non hard
        if (context_.getDifficultyConsistency().getDifficulty() != DifficultyConsistency.DifficultyLevel.Impossible) {
            //On trouve une correspondance coup joué + coup possible sur une combinaison gagnante
            for (Grid winning_grid : winning_grid_list_) {
                //Copie !
                Grid winning_grid_cpy = winning_grid.clone();

                //On va maintenant faire un parcours où on plaque les coups déjà joués sur du whatever
                for (Pair<Integer, Integer> move : already_played) {
                    if (winning_grid_cpy.isWhatever(move.first, move.second)) {
                        winning_grid_cpy.setValue(move.first, move.second, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT));
                    }
                }

                //Puis on parcours les coups possibles et on regarde si ya un whatever dedans
                for (Pair<Integer, Integer> move : possible_match) {
                    if (winning_grid_cpy.isWhatever(move.first, move.second)) {
                        //Si oui alors on le joue !
                        return new Pair<>(move.first, move.second);
                    }
                }
            }
        } else {
            //On trouve une correspondance coup joué + coup possible sur une combinaison gagnante et on compte le nombre d'occurence de ce move
            //Compteur mouvement
            Map<Pair<Integer, Integer>, Integer> moves = new HashMap<>();
            for (Grid winning_grid : winning_grid_list_) {
                //Copie !
                Grid winning_grid_cpy = winning_grid.clone();

                //On va maintenant faire un parcours où on plaque les coups déjà joués sur du whatever
                for (Pair<Integer, Integer> move : already_played) {
                    if (winning_grid_cpy.isWhatever(move.first, move.second)) {
                        winning_grid_cpy.setValue(move.first, move.second, Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT));
                    }
                }

                //Puis on parcours les coups possibles et on regarde si ya un whatever dedans
                for (Pair<Integer, Integer> move : possible_match) {
                    if (winning_grid_cpy.isWhatever(move.first, move.second)) {
                        //Si oui alors on incrémente
                        Pair<Integer, Integer> move_tmp = new Pair<>(move.first, move.second);
                        if (moves.containsKey(move_tmp)) {
                            moves.put(move_tmp, moves.get(move_tmp) + 1);
                        } else {
                            moves.put(move_tmp, 0);
                        }
                    }
                }
            }

            //On prend le max et on retourne
            int max_val = -1;
            Pair<Integer, Integer> max_key = new Pair<>(-1, -1);
            for (Map.Entry<Pair<Integer, Integer>, Integer> entry : moves.entrySet()) {
                if (entry.getValue() > max_val) {
                    max_val = entry.getValue();
                    max_key = entry.getKey();
                }
            }

            //Retour du meilleur coup possible !
            return max_key;
        }

        //Si on a rien trouvé, alors il y a un problème :/
        throw new RuntimeException("IA could not find a move !");
        //return null;
    }

    /**
     * Retourne le joueur courant
     *
     * @return
     */
    public PlayerType getCurrentPlayer() {
        return current_player_;
    }

    /**
     * Retourne vrai si la partie est finie
     *
     * @return
     */
    public boolean isFinished() {
        if (hasWinner()) {
            return true;
        } else {
            //Vérification de match nul
            for (int i = 0; i < context_.getGrid().getSize(); i++) {
                for (int j = 0; j < context_.getGrid().getSize(); j++) {
                    if (context_.getGrid().isEmpty(i, j)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Vérifie si un joueurs a gagné. Cette fonction set les valeurs gagnant et combinaison gagnante
     *
     * @return vrai s'il y a un gagnat
     */
    private boolean hasWinner() {
        lazyInit();

        context_.getGrid().dumpToConsole();

        //Check joueur courant
        PlayerType whoToCheck = getCurrentPlayer();
        Grid.CelElement whatToCheck = whoToCheck == PlayerType.Human ? MorpionActivity.PLAYER_ELEMENT : Grid.getOppositeCelElement(MorpionActivity.PLAYER_ELEMENT);
        Pair<PlayerType, Grid> winning_pair = hasWinnerInternal(whoToCheck, whatToCheck, context_.getGrid());
        if (winning_pair.first != PlayerType.None) {
            winner_ = winning_pair.first;
            winning_combination_ = winning_pair.second;
            return true;
        }

        //Check ordinateur
        winning_pair = hasWinnerInternal(getOppositePlayer(whoToCheck), Grid.getOppositeCelElement(whatToCheck), context_.getGrid());
        if (winning_pair.first != PlayerType.None) {
            winner_ = winning_pair.first;
            winning_combination_ = winning_pair.second;
            return true;
        }

        return false;
    }

    /**
     * Représente les différents types de joueur
     */
    public enum PlayerType {
        Human,
        Computer,
        None
    }
}
