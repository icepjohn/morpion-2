//Appelle le Toaster
function showToast(toast) {
    Android.showToast(toast);
}

//Ajoute un élément à la grille safe (joueur)
function addGridElemSafe(row, col, el){
    //Notification au controler Android
    if(Android.addGridElem(row, col, el)){
        $("#row-" + row + " .col-" + col + ":first")
        .empty()
        .html(
            el == "circle"
            ? $("<img src='" + window.basePath + "drawable/circle.png" + "'/>")
            : $("<img src='" + window.basePath + "drawable/cross.png" + "'/>")
        );
    }
}

//Ajotue un élément à la grille non safe (ordinateur)
function triggerAddGridElem(row, col, el){
    $("#row-" + row + " .col-" + col + ":first")
    .empty()
    .html(
        el == "circle"
        ? $("<img src='" + window.basePath + "drawable/circle.png" + "'/>")
        : $("<img src='" + window.basePath + "drawable/cross.png" + "'/>")
    );
}

//Efface la grille
function triggerCleanup(){
    $("#grid tr td").each(function(){
        $(this).empty();
    });

    var animation_duration = 0; //ms

    $("#app .crossed-line").each(function(){
        $(this).hide(animation_duration);
    });
}

//Affiche la ligne oblique sur la combinaison gagnante
function triggerCrossedLine(combination){

    var animation_duration = 0; //miliseconds

    //Diag haut gauche -> bas droite
    if(combination[0][0] == 'whatever' && combination[1][1] == 'whatever' && combination[2][2] == 'whatever')
        $("#crossed-line-diag-lt2rb").show(animation_duration);

    //Diag haut droite -> bas gauche
    if(combination[2][0] == 'whatever' && combination[1][1] == 'whatever' && combination[0][2] == 'whatever')
        $("#crossed-line-diag-rt2lb").show(animation_duration);

    //Ligne

    //Ligne du haut
    if(combination[0][0] == 'whatever' && combination[0][1] == 'whatever' && combination[0][2] == 'whatever')
        $("#crossed-line-hori-tl").show(animation_duration);

    //Ligne du milieu
    if(combination[1][0] == 'whatever' && combination[1][1] == 'whatever' && combination[1][2] == 'whatever')
        $("#crossed-line-hori-ml").show(animation_duration);

    //Ligne du bas
    if(combination[2][0] == 'whatever' && combination[2][1] == 'whatever' && combination[2][2] == 'whatever')
        $("#crossed-line-hori-bl").show(animation_duration);

    //Colonne

    //Colonne de gauche
    if(combination[0][0] == 'whatever' && combination[1][0] == 'whatever' && combination[2][0] == 'whatever')
        $("#crossed-line-vert-ll").show(animation_duration);

    //Ligne du bas
    if(combination[0][1] == 'whatever' && combination[1][1] == 'whatever' && combination[2][1] == 'whatever')
        $("#crossed-line-vert-ml").show(animation_duration);

    //Ligne du bas
    if(combination[0][2] == 'whatever' && combination[1][2] == 'whatever' && combination[2][2] == 'whatever')
        $("#crossed-line-vert-rl").show(animation_duration);
}

//Initialise les controler JS
$("document").ready(function(){
    $("#app .crossed-line").each(function(){
        $(this).hide();
    });

    //Récupération de l'objet "player" pour les binds
    var player_element = Android.getPlayerElement();
    //Bind des events sur le tableau (pour le player uniquement : l'ordi a pas besoin de cliquer !)
    $("#grid tr td").each(function(){
        $(this).click(function(){
            if(Android.canIPlay()){
                var col = $(this).attr("class").split("-")[1];
                var row = $(this).parent().attr("id").split("-")[1];
                //showToast("Clicked(" + row + ", " + col + ")");
                addGridElemSafe(Number(row), Number(col), player_element);
            };
        });
    });
});