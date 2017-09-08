package eisti.eu.MRO.morpion;

import android.util.Log;

/**
 * Projet : Morpion
 * <p/>
 * Created by Michaël on 24/09/2015.
 * <p/>
 * Class modèle pour une grille de morpion.
 */
public class Grid implements Cloneable {
    /**
     * Tag pour la console
     */
    private static final String TAG = "Grid";
    /**
     * Représentation interne de la grille
     */
    private CelElement grid_[][];

    /**
     * Constructeur, initialise la grille avec la valeur CelElement.Empty.
     *
     * @param size taille de la grille
     * @see eisti.eu.MRO.morpion.Grid.CelElement
     */
    public Grid(int size) {
        grid_ = new CelElement[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid_[i][j] = CelElement.Empty;
            }
        }
    }

    /**
     * Compare si une grille normale (avec valeurs CelElement.Circle ou CelElement.Cross) trouve une correspondance
     * dans une grille type Whatever
     *
     * @param expected la valeur que l'on veut comparer (soit CelElement.Circle soit CelElement.Cross
     * @param lhs      la valeur de la grille whatever
     * @param rhs      ma valeur de la grille normale
     * @return résultat de la comparaison
     * @see eisti.eu.MRO.morpion.Grid.CelElement
     */
    public static boolean isCelValueEqual(CelElement expected, CelElement lhs, CelElement rhs) {
        return lhs == CelElement.Whatever && rhs == expected;
    }

    /**
     * Retourne la valeur opposée à la valeur entrée
     * Par convention CelElement.Cross oppose CelElement.Circle
     *
     * @param cel valeur dont il faut trouver l'opposé
     * @return valeur opposée ou null
     */
    public static CelElement getOppositeCelElement(CelElement cel) {
        if (cel == CelElement.Empty || cel == CelElement.Whatever)
            return null;
        return cel == CelElement.Circle ? CelElement.Cross : CelElement.Circle;
    }

    /**
     * Retourne de la taille de la grille
     *
     * @return taille de la grille
     */
    public int getSize() {
        return grid_.length;
    }

    /**
     * Compte le nombre de cellules vides dans la grille
     *
     * @return nom de cellules vides
     */
    public int getEmptyCelNumber() {
        int num = 0;
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                num += isEmpty(i, j) ? 1 : 0;
            }
        }
        return num;
    }

    /**
     * Vérifie si la case est vide
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @return résultat de la vérification
     */
    public boolean isEmpty(int row, int col) {
        return grid_[row][col] == CelElement.Empty;
    }

    /**
     * Vérifie si la case est un cercle
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @return résultat de la vérification
     */
    public boolean isCircle(int row, int col) {
        return grid_[row][col] == CelElement.Circle;
    }

    /**
     * Vérifie si la case est une croix
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @return résultat de la vérification
     */
    public boolean isCross(int row, int col) {
        return grid_[row][col] == CelElement.Circle;
    }

    /**
     * Vérifie si la case est le placeholder
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @return résultat de la vérification
     */
    public boolean isWhatever(int row, int col) {
        return grid_[row][col] == CelElement.Whatever;
    }

    /**
     * Vérifie si la valeur à l'emplacement donnée et la valeur en paramètre sont égales
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @param v   valeur à comparer
     * @return résultat de la comparaison
     */
    public boolean isEqual(int row, int col, CelElement v) {
        return getValue(row, col) == v;
    }

    /**
     * Récupère la valeur de la case
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @return valeur
     */
    public CelElement getValue(int row, int col) {
        return grid_[row][col];
    }

    /**
     * Assigne une valeur à la case
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @param el  valeur à assigner
     */
    public void setValue(int row, int col, CelElement el) {
        grid_[row][col] = el;
    }

    /**
     * Assigne une valeur à la case si celle-ci est vide
     *
     * @param row numéro de la colonne
     * @param col numéro de la ligne
     * @param el  valeur à assigner
     * @return vrai si l'assignement a pu avoir lieu
     */
    public boolean setValueSafe(int row, int col, CelElement el) {
        if (isEmpty(row, col)) {
            grid_[row][col] = el;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fonction utilitaire pour afficher la valeur d'une grille dans la console Log
     */
    public void dumpToConsole() {
        for (int i = 0; i < getSize(); i++) {
            String buffer = "";
            for (int j = 0; j < getSize(); j++) {
                buffer += getValue(i, j).toString() + " ";
            }
            Log.i(TAG, buffer);
        }
        Log.i(TAG, "");
    }

    /**
     * Retourne la grille sous forme Javascript Array
     *
     * @return string avec les valeurs en JS
     */
    public final String asJSArray() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String separator = "";
        for (int i = 0; i < getSize(); i++) {
            sb.append(separator).append("[");
            separator = "";
            for (int j = 0; j < getSize(); j++) {
                sb.append(separator).append("'").append(grid_[i][j].toString().toLowerCase()).append("'");
                separator = ", ";
            }
            separator = ", ";
            sb.append("]");
        }
        sb.append("]");
        Log.i(TAG, "JS version=" + sb.toString());
        return sb.toString();
    }

    /**
     * Clone la grille
     *
     * @return copie de la grille
     */
    @Override
    public Grid clone() {
        Grid cln = new Grid(getSize());
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                cln.grid_[i][j] = grid_[i][j];
            }
        }
        return cln;
    }

    /**
     * Type d'éléments possibles dans la grille
     * Whatever est un placeholder pour dire que ce n'est pas vide
     */
    public enum CelElement {
        Circle, Cross, Empty,
        Whatever //utilisé dans le check des combinaisons gagnantes (soit Circle, soit cross -> tableau génériques)
    }
}
