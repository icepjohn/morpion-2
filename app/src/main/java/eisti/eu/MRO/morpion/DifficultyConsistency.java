package eisti.eu.MRO.morpion;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Projet : Morpion
 * <p/>
 * Created by Michaël on 24/09/2015.
 * <p/>
 * Classe gérant la cohérence dans les différents niveaux de difficulté possibles
 */
public class DifficultyConsistency implements View.OnTouchListener {
    /**
     * Tag pour la console
     */
    private static final String TAG = "DifficultyConsistency";
    /**
     * Liste des boutons
     */
    private final ArrayList<ToggleButton> buttons_;
    /**
     * Contexte
     */
    private final MorpionActivity context_;
    /**
     * Difficulté courante
     */
    private DifficultyLevel difficulty_;

    /**
     * @param ctx        contrexte
     * @param difficulty difficulté par défaut
     * @param buttons    liste des boutons
     */
    public DifficultyConsistency(MorpionActivity ctx, DifficultyLevel difficulty, ArrayList<ToggleButton> buttons) {
        difficulty_ = difficulty;
        buttons_ = buttons;
        context_ = ctx;

        for (ToggleButton button : buttons_) {
            button.setOnTouchListener(this);
        }

        //Première consistence visuelle manuelle
        context_.getButtonEasy().setChecked(difficulty_ == DifficultyLevel.Easy);
        context_.getButtonNormal().setChecked(difficulty_ == DifficultyLevel.Normal);
        context_.getButtonImpossible().setChecked(difficulty_ == DifficultyLevel.Impossible);
    }

    /**
     * Récupère la difficulté courante
     *
     * @return difficulté courante
     */
    public DifficultyLevel getDifficulty() {
        return difficulty_;
    }

    /**
     * @param v     bouton appuyé
     * @param event propriétés de l'event
     * @return faux si aucun des boutons n'a été appuyé, vrai sinon
     * @see android.view.View.OnTouchListener
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ToggleButton current_button = (ToggleButton) v;
        if (current_button == context_.getButtonEasy()) {
            difficulty_ = DifficultyLevel.Easy;
        } else if (current_button == context_.getButtonNormal()) {
            difficulty_ = DifficultyLevel.Normal;
        } else if (current_button == context_.getButtonImpossible()) {
            difficulty_ = DifficultyLevel.Impossible;
        } else {
            return false;
        }

        Log.i(TAG, "Difficulty set to : " + String.valueOf(difficulty_));

        return updateViewConsistency(current_button);
    }

    /**
     * Met à jour la consistence visuel des boutons (1 seul alumé à la fois)
     *
     * @param current_button bouton qui a été appuyé
     * @return toujours vrai : on n'autorise pas à ce qu'il n'y ait aucun bouton coché
     */
    @SuppressWarnings("SameReturnValue")
    private boolean updateViewConsistency(ToggleButton current_button) {
        for (ToggleButton button : buttons_) {
            if (button.isChecked()) {
                if (button == current_button) { //Si le bouton alumé est celui qui a été appuyé
                    Log.i(TAG, "Button '" + String.valueOf(button.getText()) + "' unchanged.");
                    //Si return false alors on peut n'avoir aucun bouton d'activé -> mauvais comportement
                    return true;
                } else {
                    button.setChecked(false);
                    Log.i(TAG, "Button '" + String.valueOf(button.getText()) + "' disabled.");
                }
            }
        }
        current_button.setChecked(true);
        Log.i(TAG, "Button '" + String.valueOf(current_button.getText()) + "' enabled.");
        return true;
    }

    /**
     * Définie les différents types de difficulté
     */
    public enum DifficultyLevel {
        Easy, Normal, Impossible
    }
}
