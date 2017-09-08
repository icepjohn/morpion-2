package eisti.eu.MRO.morpion;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.Collection;


/**
 * Projet : Morpion
 * <p/>
 * Created by Michaël on 23/09/2015.
 * <p/>
 * Classe qui gère l'interopérabilité de la webview et de la main activity grâce au javascript
 */
public class WebApp {
    /**
     * Tag pour la console
     */
    private static final String TAG = "WebApp";

    /**
     * Contexte
     */
    private MorpionActivity context_;

    /**
     * Contruit un nouveau controler pour gérer la webview
     *
     * @param c contexte
     */
    public WebApp(MorpionActivity c) {
        context_ = c;
    }

    /**
     * Appelle une fonction javascript
     * /*\ ATTENTION /*\
     * Cette fonction doit être appellée depuis le thread qui gère view
     * Si vous ne vous y trouvez pas, ou risquez de ne pas vous y trouver suivant où vous êtes appellé, faites comme ceci :
     * <p/>
     * <p><blockquote><pre>
     * myWebView.post(new Runnable() {
     *      {@literal @}Override
     *      public void run(){
     *          myWebApp.callJavascript(...);
     *      }
     * });
     * </pre></blockquote></p>
     *
     * @param view       webview sur laquelle interragir
     * @param methodName nom de la fonction javascript à appeller
     * @param raw        true si les arguments doivent pris de manière brutte. false s'ils doivent être formatés
     * @param params     paramètres à injecter dans la fonction javascript
     */
    public static void callJavaScript(WebView view, String methodName, boolean raw, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:try{").append(methodName).append("(");
        String sep = "";
        //Max 3 niveaux
        if (raw) {
            for (Object param : params) {
                sb.append(sep);
                sep = ", ";
                sb.append(param.toString());
            }
        } else {
            for (Object param : params) {
                sb.append(sep);
                sep = ",";
                if (param instanceof Collection) {
                    sb.append("[");
                    sep = "";
                    for (Object p : (Collection) param) {
                        sb.append(sep);
                        sep = ",";
                        if (p instanceof Collection) {
                            sb.append("[");
                            sep = "";
                            for (Object _ : (Collection) p) {
                                sb.append(sep);
                                sep = ",";
                                sb.append("'").append(_.toString()).append("'");
                            }
                            sb.append("]");
                        } else {
                            sb.append("'").append(p.toString()).append("'");
                        }
                    }
                    sb.append("]");
                } else {
                    sb.append("'").append(param.toString()).append("'");
                }
            }
        }
        sb.append(")}catch(error){console.error(error.message);}");
        final String call = sb.toString();
        Log.i(TAG, "callJavaScript: call=" + call);

        view.loadUrl(call);
    }

    /**
     * Initialise une nouvelle partie dans la webView
     */
    public void newGame() {
        callJavaScript(context_.getWebView(), "triggerCleanup", false);
    }

    /**
     * Montre la ligne barré sur la combinaison gagnante dans la webview
     */
    public void showCrossedLine() {
        callJavaScript(context_.getWebView(), "triggerCrossedLine", true, context_.getGameEngine().getWinningCombination().asJSArray());
    }

    /**
     * Demande l'affichage dans la webView d'un élément dans la grille
     *
     * @param row numéro de ligne
     * @param col numéro de colonne
     * @param el  élément à afficher : "circle" ou "cross"
     */
    public void showGridElem(int row, int col, Grid.CelElement el) {
        callJavaScript(context_.getWebView(), "triggerAddGridElem", true, row, col, el == Grid.CelElement.Circle ? "'circle'" : "'cross'");
    }

    /**
     * Forward une demande d'affichage de toaster depuis le javascript
     *
     * @param text texte à toaster
     */
    @JavascriptInterface
    public void showToast(String text) {
        context_.showToast(text);
    }

    /**
     * Forward une demande d'ajout dans la grille du game engine depuis le javascript
     * Effectue également l'appelle au tour suivant (le joueur joue avec cette méthode)
     *
     * @param row numéro de ligne
     * @param col numéro de colonne
     * @param el  élément joué "circle" ou "cross"
     * @return vrai si le joueur peut jouer à cet endroit (pour le javascript)
     */
    @JavascriptInterface
    public boolean addGridElem(int row, int col, String el) {
        Log.i(TAG, "Ajout index element : (" + row + ", " + col + ")=" + el);
        boolean allowed = context_.getGrid().setValueSafe(row, col, el.equals("circle") ? Grid.CelElement.Circle : Grid.CelElement.Cross);
        if (!allowed) {
            context_.showToast(context_.getResources().getString(R.string.toast_cell_already_used));
        } else {
            context_.getGameEngine().cancelDelayedToaster();
            context_.getWebView().post(new Runnable() {
                @Override
                public void run() {
                    context_.getGameEngine().swapTurn();
                }
            });
        }
        return allowed;
    }

    /**
     * Retourne l'élément que le joueur joue (forward depuis le contexte)
     *
     * @return élément que le joueur joue
     */
    @JavascriptInterface
    public String getPlayerElement() {
        return MorpionActivity.PLAYER_ELEMENT == Grid.CelElement.Circle ? "circle" : "cross";
    }

    /**
     * Retourne vrai si le joueur a le droit de jouer
     * Pendant le tour de l'ordi, cette méthode renvoie faux
     *
     * @return vrai si le joueur peut jouer
     */
    @JavascriptInterface
    public boolean canIPlay() {
        return context_.getGameEngine().getCurrentPlayer() == GameEngine.PlayerType.Human;
    }
}
