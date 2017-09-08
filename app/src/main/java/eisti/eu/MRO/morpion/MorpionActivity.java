package eisti.eu.MRO.morpion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Projet : Morpion
 * <p/>
 * Created by Michaël on 22/09/2015.
 * <p/>
 * Point d'entrée de l'application. Controler principal
 * Instancie les modèles, les vues et les controlers auxilières et lie le tout.
 */
public class MorpionActivity extends AppCompatActivity {

    /**
     * Configuration du jeu par défaut
     * Element que le joueur joue
     */
    public static final Grid.CelElement PLAYER_ELEMENT = Grid.CelElement.Circle;
    /**
     * Configuration du jeu par défaut
     * Difficulté sélectionnée par défaut
     */
    public static final DifficultyConsistency.DifficultyLevel DEFAULT_DIFFICULTY = DifficultyConsistency.DifficultyLevel.Normal;
    /**
     * Configuration du jeu par défaut
     * Joueur qui commence à jouer
     */
    public static final GameEngine.PlayerType DEFAULT_FIRST_PLAYER = GameEngine.PlayerType.Human;
    /**
     * TAG pour la console
     */
    private static final String TAG = "MorpionActivity";
    /**
     * Permet de savoir si on peut toaster (l'appli a pas le focus : on toast pas)
     */
    private boolean is_ui_foreground_;

    //Models
    /**
     * Grille du jeu
     */
    private Grid grid_;
    /**
     * Moteur du jeu
     */
    private GameEngine game_engine_;

    //Controler
    /**
     * Controler de la WebView
     */
    private WebApp web_app_;
    /**
     * Controler de la cohérence de l'affichage de la difficulté
     */
    private DifficultyConsistency difficulty_consistency_;

    //Vues
    /**
     * WebView de la grille
     */
    private WebView web_view_;

    /**
     * Bouton easy
     */
    private ToggleButton button_easy_;
    /**
     * Bouton normal
     */
    private ToggleButton button_normal_;
    /**
     * Bouton impossible
     */
    private ToggleButton button_impossible_;

    /**
     * Compteur du numbre de partie
     */
    private TextView text_game_counter_;
    /**
     * Compteur du score du joueur
     */
    private TextView text_score_player_;
    /**
     * Compteur du score de l'ordinateur
     */
    private TextView text_score_opponent_;

    /*
    Getters
     */

    //Models

    /**
     * Retourne la grille
     *
     * @return grille
     */
    public Grid getGrid() {
        return grid_;
    }

    /**
     * Retourne le moteur de jeu
     *
     * @return moteur de jeu
     */
    public GameEngine getGameEngine() {
        return game_engine_;
    }

    //Controler

    /**
     * Retourne le controler de la web view
     *
     * @return controler de la web view
     */
    public WebApp getWebApp() {
        return web_app_;
    }

    /**
     * Retourne le controler de la cohérence de difficulté
     *
     * @return controler de la cohérence de difficulté
     */
    public DifficultyConsistency getDifficultyConsistency() {
        return difficulty_consistency_;
    }

    //Vues

    /**
     * Retourne la web view (grille)
     *
     * @return web view (grille)
     */
    public WebView getWebView() {
        return web_view_;
    }

    /**
     * Retourne le bouton easy
     *
     * @return bouton easy
     */
    public ToggleButton getButtonEasy() {
        return button_easy_;
    }

    /**
     * Retourne le bouton normal
     *
     * @return bouton normal
     */
    public ToggleButton getButtonNormal() {
        return button_normal_;
    }

    /**
     * Retourne le bouton impossible
     *
     * @return bouton impossible
     */
    public ToggleButton getButtonImpossible() {
        return button_impossible_;
    }


    /**
     * Fonction principale : lance le programme
     *
     * @param savedInstanceState élément sérialisé représentant l'état de l'appli
     */
    @Override
    //Avertissement de sécurité concernant l'activation de Javascript
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morpion);

        //Si le programme est en intention d'"EXIT" on ne fait rien
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        //On est en premier plan
        is_ui_foreground_ = true;

        //Initialisation des models
        grid_ = new Grid(3);
        game_engine_ = new GameEngine(this, DEFAULT_FIRST_PLAYER);

        Log.i(TAG, "Initialisation des modèles terminée.");

        //Chargement des vues score
        text_score_opponent_ = (TextView) findViewById(R.id.score_opponent);
        text_score_player_ = (TextView) findViewById(R.id.score_player);
        text_game_counter_ = (TextView) findViewById((R.id.game_counter));

        Log.i(TAG, "Chargement des vues des scores terminé.");

        //Initialisation de la web view et de son engine
        web_app_ = new WebApp(this);
        web_view_ = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = web_view_.getSettings();
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        web_view_.addJavascriptInterface(web_app_, "Android");

        web_view_.loadUrl("file:///android_res/raw/grid.html");

        Log.i(TAG, "Chargement de la vue de la grille terminé.");

        //Initialisation des vues button et association du engin de cohérence
        //Le projet est trop petit pour penser à faire un mediator
        button_easy_ = (ToggleButton) findViewById(R.id.button_easy);
        button_normal_ = (ToggleButton) findViewById(R.id.button_normal);
        button_impossible_ = (ToggleButton) findViewById(R.id.button_impossible);

        difficulty_consistency_ = new DifficultyConsistency(this, DEFAULT_DIFFICULTY, new ArrayList<>(Arrays.asList(button_easy_, button_impossible_, button_normal_)));

        Log.i(TAG, "Chargement du engin de difficulté terminé.");

        //On lance la première partie 2 secondes après le lancement
        showToast(getString(R.string.first_game_message));
        Handler launcher = new Handler();
        launcher.postDelayed(new Runnable() {
            public void run() {
                Log.i(TAG, "Lancement de la première partie...");
                newGame();
            }
        }, 2000);
    }

    /**
     * Méthode générée par défaut : désérialise la création du menu
     *
     * @param menu menu à desérialiser
     * @return vrai si on affiche le menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_morpion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener du menu
     *
     * @param item item sélectionné
     * @return vrai si on consome l'élément (le menu se referme après)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            //Pas bien de faire comme ça !
            //System.exit(0);
            //Il vaut mieux faire comme ça !
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Event trigger quand l'appli revient au premier plan
     */
    @Override
    protected void onResume() {
        super.onResume();
        is_ui_foreground_ = true;
    }

    /**
     * Event trigger quand l'appli est mise en pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        is_ui_foreground_ = false;
    }

    /**
     * Toaster de l'appli : uniquement si on a le focus
     */
    public void showToast(String text) {
        if (is_ui_foreground_) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Finit la partie en cours et en relance une autre
     */
    public void finishGame() {
        //On poste pour ne pas avoir l'erreur comme quoi on lance depuis un autre thread :
        //>> W/WebView﹕ java.lang.Throwable: A WebView method was called on thread 'JavaBridge'. All WebView methods must be called on the same thread.
        //Ce qui arrivera car toute la routine est asynchrone à la vue
        web_view_.post(new Runnable() {
            @Override
            public void run() {
                //Mettre la bare dans la webView
                if (game_engine_.getWinner() != GameEngine.PlayerType.None) { //Pas de bare pour un match nul
                    web_app_.showCrossedLine();
                }
                //Affichage des scores
                text_game_counter_.setText(String.valueOf(game_engine_.getGameCounter()));
                text_score_player_.setText(String.valueOf(game_engine_.getScorePlayer()));
                text_score_opponent_.setText(String.valueOf(game_engine_.getScoreOpponent()));
            }
        });

        //Lance la partie suivante
        showToast(getString(R.string.next_game_message));
        Handler launcher = new Handler();
        launcher.postDelayed(new Runnable() {
            public void run() {
                Log.i(TAG, "Lancement de la partie suivante...");
                newGame();
            }
        }, 3000);
    }

    /**
     * Lance une nouvelle partie
     */
    public void newGame() {
        //On poste pour ne pas avoir l'erreur comme quoi on lance depuis un autre thread :
        //>> W/WebView﹕ java.lang.Throwable: A WebView method was called on thread 'JavaBridge'. All WebView methods must be called on the same thread.
        //Ce qui arrivera car toute la routine est asynchrone à la vue
        web_view_.post(new Runnable() {
            @Override
            public void run() {
                web_app_.newGame();
            }
        });
        grid_ = new Grid(3);
        game_engine_.newGame();
    }
}
